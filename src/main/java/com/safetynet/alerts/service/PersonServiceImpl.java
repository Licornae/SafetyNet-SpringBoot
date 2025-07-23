
package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService {
    @Override
    public Person getPerson(String firstName, String lastName) {
        if (firstName.equals("John") && lastName.equals("Boyd")) {
            Person person = new Person();
            person.setFirstName("John");
            person.setLastName("Boyd");
            person.setAddress("1509 Culver St");
            person.setCity("Culver");
            person.setZip("97451");
            person.setPhone("841-874-6512");
            person.setEmail("jaboyd@email.com");
            return person;
        }
        return null;
    }
}