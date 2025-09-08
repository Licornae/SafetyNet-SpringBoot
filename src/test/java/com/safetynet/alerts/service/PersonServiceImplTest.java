package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
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
        // Arrange
        String firstName = "John";
        String lastName = "Boyd";

        // Act
        Person person = personService.getPerson(firstName, lastName);

        // Assert
        assertNotNull(person, "The search person should exist");
        assertEquals("John", person.getFirstName());
        assertEquals("Boyd", person.getLastName());
        assertEquals("1509 Culver St",person.getAddress());
        assertEquals("Culver",person.getCity());
        assertEquals("97451",person.getZip());
        assertEquals("841-874-6512", person.getPhone());
        assertEquals("jaboyd@email.com", person.getEmail());
    }

    @Test
    public void testWhenDeletingExistingPerson_thenItsRemoved() {
        // Arrange
        String firstName = "John";
        String lastName = "Boyd";
        Person person = personService.getPerson(firstName, lastName);
        assertNotNull(person, "Record must exist before deletion");

        // Act
        boolean deleted = personService.deletePerson("John", "Boyd");

        // Assert
        assertTrue(deleted, "Should return true for an existing person");
        assertNull(personService.getPerson("John", "Boyd"), "Person should not exist after deletion");
    }


    @Test
    public void testWhenDeletingUnknownPerson_thenReturnFalse() {
        // Arrange
        String firstName = "Unknown";
        String lastName = "Person";

        // Act
        boolean deleted = personService.deletePerson(firstName, lastName);

        // Assert
        assertFalse(deleted, "Deleting an unknown person should return false");
    }

}
