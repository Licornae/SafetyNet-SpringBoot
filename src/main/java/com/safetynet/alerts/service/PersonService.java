package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;

public interface PersonService {
    Person getPerson(String firstName, String lastName);

    Person addPerson(Person person);

    Person updatePerson(String firstName, String lastName, Person person);

    boolean deletePerson(String firstName, String lastName);
}