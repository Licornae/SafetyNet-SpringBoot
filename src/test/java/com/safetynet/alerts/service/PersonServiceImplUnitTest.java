package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.DuplicatePersonException;
import com.safetynet.alerts.exception.PersonNotFoundException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.safetynet.alerts.repository.DataRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PersonServiceImplUnitTest {

    @Mock
    DataRepository dataRepository;

    @InjectMocks
    PersonServiceImpl service;

    private DataContainer container;
    private List<Person> persons;

    private Person johnBoyd;
    private Person jacobBoyd;

    @BeforeEach
    public void setUp() {

        johnBoyd = new Person("John", "Boyd", "1509 Culver St", "Culver", "97451",
                "841-874-6512", "jaboyd@email.com");

        jacobBoyd = new Person("Jacob", "Boyd", "1509 Culver St", "Culver", "97451",
                "841-874-6513", "drk@email.com");

        persons = new ArrayList<>(of(johnBoyd, jacobBoyd));

        container = new DataContainer();
        container.setPersons(persons);

        when(dataRepository.getDataContainer()).thenReturn(container);
    }

    // GET
    @Test
    public void getPerson_Existing_ReturnsPerson() {
        Person person = service.getPerson("John", "Boyd");

        assertNotNull(person, "The searched person should exist");
        assertEquals("John", person.getFirstName());
        assertEquals("Boyd", person.getLastName());
        assertEquals("1509 Culver St", person.getAddress());
        assertEquals("Culver", person.getCity());
        assertEquals("97451", person.getZip());
        assertEquals("841-874-6512", person.getPhone());
        assertEquals("jaboyd@email.com", person.getEmail());
    }

    @Test
    public void getPerson_unknown_returnsNull() {
        assertNull(service.getPerson("Unknown", "Person"));
    }

    // ADD
    @Test
    public void addPerson_CreatesNewPerson() {
        Person newPerson = new Person("Jane", "Doe", "56 River St", "Culver", "97451",
                "841-874-7650", "janedoe@email.com");

        Person saved = service.addPerson(newPerson);

        assertSame(newPerson, saved, "Service should return the same instance it added");
        assertTrue(container.getPersons().contains(newPerson));
        assertEquals(3, container.getPersons().size());
    }

    @Test
    public void addPerson_Duplicate_ThrowsDuplicatePersonException() {
        Person existing = new Person("John","Boyd","address","city","zip","phone","mail@mail.com");
        DataContainer container = new DataContainer();
        container.setPersons(new ArrayList<>(List.of(existing)));
        when(dataRepository.getDataContainer()).thenReturn(container);

        Person input = new Person("John","Boyd","a","c","z","p","m@mail.com");
        assertThrows(DuplicatePersonException.class, () -> service.addPerson(input));
    }

    // UPDATE
    @Test
    public void updatePerson_Existing_UpdatesAllFields() {
        Person payload = new Person("John", "Boyd", "54 River St", "Culver", "97451",
                "842-874-7660", "johnaboyd@wanadoo.com");

        Person updated = service.updatePerson("John", "Boyd", payload);

        assertSame(johnBoyd, updated, "Service should update and return the stored instance");
        assertEquals("54 River St", updated.getAddress());
        assertEquals("Culver", updated.getCity());
        assertEquals("97451", updated.getZip());
        assertEquals("842-874-7660", updated.getPhone());
        assertEquals("johnaboyd@wanadoo.com", updated.getEmail());
    }

    @Test
    public void updatePerson_Unknown_ThrowsNotFound() {
        Person payload = new Person("John", "Unknown", "54 River St", "Culver", "97451",
                "842-874-7660", "johnaboyd@wanadoo.com");

        assertThrows(PersonNotFoundException.class,
                () -> service.updatePerson("John", "Unknown", payload));
    }

    // DELETE
    @Test
    public void deletePerson_Existing_Removes() {
        boolean deleted = service.deletePerson("John", "Boyd");
        assertTrue(deleted);
        assertNull(service.getPerson("John", "Boyd"), "Person should not exist after deletion");
        assertEquals(1, container.getPersons().size());
    }

    @Test
    public void deletePerson_Unknown_ReturnsFalse() {
        int before = container.getPersons().size();
        boolean deleted = service.deletePerson("Unknown", "Person");
        assertFalse(deleted);
        assertEquals(before, container.getPersons().size(), "No person should be removed");
    }


}
