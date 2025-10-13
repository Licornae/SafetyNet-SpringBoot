package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing CRUD operations for Person resources.
 * Endpoint:
 * - POST /person — Create a new person profil
 * - PUT /person/{firstName}/{lastName} — Update an existing person
 * - DELETE /person/{firstName}/{lastName} — Delete an existing person
 */
@Slf4j
@Validated
@RestController
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Creates a new person.
     * Method: POST
     * Path: /person
     * Response:
     * - 201 Created with the Person in the response body on success.
     * - 400 Bad Request if the payload is invalid.
     * - 409 Conflict if the person already exists (same firstName + lastName).
     *
     * @param person the person to create must be valid and contain required fields
     * @return ResponseEntity with status 201 and the created Person
     */
    @PostMapping("/person")
    public ResponseEntity<?> addPerson(@Valid @RequestBody Person person) {
        log.info("POST /person for: {}", person);

        Person created = personService.addPerson(person);

        log.info("Person created for: {} {}", created.getFirstName(), created.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates an existing person identified by first and last name.
     * Method: PUT
     * Path: /person/{firstName}/{lastName}
     * Response:
     * - 200 OK with the updated Person on success.
     * - 400 Bad Request if the payload is invalid.
     * - 404 Not Found if no person matches the provided identifiers.
     *
     * @param firstName the first name identifying the person
     * @param lastName  the last name identifying the person
     * @param person    the updated payload must be valid
     * @return ResponseEntity with status 200 and the updated Person
     */
    @PutMapping(value = "/person/{firstName}/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> updatePerson(
            @PathVariable String firstName,
            @PathVariable String lastName,
            @Valid @RequestBody Person person) {
        log.info("PUT /person/{}/{}", firstName, lastName);

        //path and body consistency check
        if ((person.getFirstName() != null && !person.getFirstName().equals(firstName))
                || (person.getLastName() != null && !person.getLastName().equals(lastName))) {
            log.error("Path/body Person mismatch. path='{} {}', body='{}{}'", firstName, lastName, person.getFirstName(), person.getLastName());
            throw new IllegalArgumentException("Path {firstName,lastName} must match body.");
        }

        Person updated = personService.updatePerson(firstName, lastName, person);
        log.info("Person profile updated for: {} {}", updated.getFirstName(), updated.getLastName());

        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a person identified by first and last name.
     * Method: DELETE
     * Path: /person/{firstName}/{lastName}
     * Response:
     * - 204 No Content when the person is successfully deleted.
     * - 404 Not Found when no person matches the provided identifiers.
     *
     * @param firstName the first name identifying the person
     * @param lastName  the last name identifying the person
     * @return ResponseEntity with status 204 if deletion succeeded, or 404 otherwise
     */
    @DeleteMapping("/person/{firstName}/{lastName}")
    public ResponseEntity<Void> deletePerson(
            @PathVariable String firstName,
            @PathVariable String lastName) {
        log.info("DELETE /person/{}/{}", firstName, lastName);

        boolean deleted = personService.deletePerson(firstName, lastName);
        if(deleted) {
            log.info("Deleted person '{} {}'", firstName, lastName);
            return ResponseEntity.noContent().build();
        }
        log.error("Deletion impossible, person not found: {} {}", firstName, lastName);

        return ResponseEntity.notFound().build();
    }
}
