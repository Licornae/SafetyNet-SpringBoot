package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Person> addPerson(@RequestBody Person person) {
        Person savedPerson = personService.addPerson(person);
        return new ResponseEntity<>(savedPerson, HttpStatus.CREATED);
    }
}
