package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PersonServiceImplTest {

    @Autowired
    private PersonService personService;

    @Test
    public void whenFindingExistingPerson_thenReturnsCorrectPerson() {
        // Arrange - ces infos doivent exister dans data.json
        String firstName = "John";
        String lastName = "Boyd";

        // Act
        Person person = personService.getPerson(firstName, lastName);

        // Assert
        assertNotNull(person, "La personne recherchée doit exister");
        assertEquals("John", person.getFirstName());
        assertEquals("Boyd", person.getLastName());
        assertEquals("1509 Culver St",person.getAddress());
        assertEquals("Culver",person.getCity());
        assertEquals("97451",person.getZip());
        assertEquals("841-874-6512", person.getPhone());
        assertEquals("jaboyd@email.com", person.getEmail());
    }

    @Test
    public void whenFindingUnknownPerson_thenReturnsNull() {
        // Arrange
        String firstName = "Nonexistent";
        String lastName = "Nobody";

        // Act
        Person person = personService.getPerson(firstName, lastName);

        // Assert
        assertNull(person, "Une personne inconnue doit renvoyer null");
    }
}
