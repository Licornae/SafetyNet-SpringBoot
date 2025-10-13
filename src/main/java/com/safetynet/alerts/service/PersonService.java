package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;

/**
 * Service API for managing Person entities.
 * Contract:
 * - addPerson: throws DuplicatePersonException if (firstName, lastName) already exists.
 * - updatePerson: throws PersonNotFoundException if the target person does not exist.
 * - deletePerson: returns true when a person is removed, false otherwise.
 */
public interface PersonService {

    /**
     * Returns the person matching the given identity, or null when not found.
     *
     * @param firstName the person's first name must not be blank
     * @param lastName  the person's last name must not be blank
     * @return the matching Person, or null if none exists
     */
    Person getPerson(String firstName, String lastName);

    /**
     * Creates a new person.
     * Contract: Implementations should prevent duplicates for the same {firstName, lastName}.
     *
     * @param person the person to create must not be null and should contain required fields
     * @return the created Person
     */
    Person addPerson(Person person);

    /**
     * Updates an existing person identified by first and last name.
     * Contract:
     * - The person to update is identified by the path parameters {firstName, lastName}.
     * - Implementations may merge or replace fields based on business rules.
     *
     * @param firstName the first name identifying the person to update must not be blank
     * @param lastName  the last name identifying the person to update must not be blank
     * @param person    the payload containing fields to update must not be null
     * @return the updated Person
     */
    Person updatePerson(String firstName, String lastName, Person person);


    /**
     * Deletes a person identified by first and last name.
     *
     * @param firstName the first name identifying the person must not be blank
     * @param lastName  the last name identifying the person must not be blank
     * @return true if a person was deleted, false if no matching person exists
     */
    boolean deletePerson(String firstName, String lastName);
}