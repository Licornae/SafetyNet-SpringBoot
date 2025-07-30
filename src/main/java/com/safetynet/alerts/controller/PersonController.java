package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/person")
    public Person getPerson(@RequestParam String firstName, @RequestParam String lastName){
        return personService.getPerson(firstName, lastName);
    }

    @PostMapping("/person")
    public ResponseEntity<?> addPerson(@Valid @RequestBody Person person) {
        // Évite d’ajouter un doublon
        Person existing = personService.getPerson(person.getFirstName(), person.getLastName());
        if (existing != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cette personne existe déjà");
        }

        Person savedPerson = personService.addPerson(person);
        return new ResponseEntity<>(savedPerson, HttpStatus.CREATED);
    }

    @PutMapping(value = "/person/{firstName}/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> updatePerson(
            @PathVariable String firstName,
            @PathVariable String lastName,
            @Valid @RequestBody Person person) {

        Person updated = personService.updatePerson(firstName, lastName, person);
        return ResponseEntity.ok(updated);
    }
}
