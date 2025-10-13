
package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.DataNotLoadedException;
import com.safetynet.alerts.exception.DuplicatePersonException;
import com.safetynet.alerts.exception.PersonNotFoundException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Default implementation of PersonService backed by DataRepository.
 */
@Slf4j
@Service
public class PersonServiceImpl implements PersonService {

    private final DataRepository dataRepository;

    @Autowired
    public PersonServiceImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person getPerson(String firstName, String lastName) {
        log.debug("getPerson called with firstName='{}', lastName='{}'", firstName, lastName);

        DataContainer container = dataRepository.getDataContainer();
        if (container == null || container.getPersons() == null) {
            log.error("getPerson: DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        Person result = container.getPersons().stream()
                .filter(person -> person.getFirstName().equals(firstName)
                        && person.getLastName().equals(lastName))
                .findFirst()
                .orElse(null);

        if (result == null) {
            log.debug("getPerson: no record found for '{} {}'", firstName, lastName);
        } else {
            log.debug("getPerson: record found for '{} {}'", firstName, lastName);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person addPerson(Person person) {
        log.debug("addPerson called for '{} {}'", person.getFirstName(), person.getLastName());

        Person existing = getPerson(person.getFirstName(), person.getLastName());
        if (existing != null) {
            log.error("addPerson: duplicate for '{} {}'", person.getFirstName(), person.getLastName());
            throw new DuplicatePersonException("This person already exists: " + person.getFirstName() + " " + person.getLastName());
        }
        dataRepository.getDataContainer().getPersons().add(person);
        log.debug("addPerson: created record for: {} {}", person.getFirstName(), person.getLastName());

        return person;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person updatePerson(String firstName, String lastName, Person updatedPerson) {
        log.debug("updatePerson called for '{} {}'", firstName, lastName);

        DataContainer container = dataRepository.getDataContainer();
        if (container == null || container.getPersons() == null) {
            log.error("updatePerson: DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        List<Person> persons = container.getPersons();

        for (Person person : persons) {
            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                person.setAddress(updatedPerson.getAddress());
                person.setCity(updatedPerson.getCity());
                person.setZip(updatedPerson.getZip());
                person.setPhone(updatedPerson.getPhone());
                person.setEmail(updatedPerson.getEmail());
                return person;
            }
        }
        log.error("updatePerson: record not found for '{} {}'", firstName, lastName);
        throw new PersonNotFoundException("Person not found: " + firstName + " " + lastName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deletePerson(String firstName, String lastName) {
        log.debug("deletePerson called for '{} {}'", firstName, lastName);

        DataContainer container = dataRepository.getDataContainer();
        if (container == null || container.getPersons() == null) {
            log.error("deletePerson: DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        List<Person> persons = container.getPersons();

        boolean personRemoved = persons.removeIf(person ->
                person != null
                        && firstName.equals(person.getFirstName())
                        && lastName.equals(person.getLastName())
        );

        if (personRemoved) {
            log.info("deletePerson: deleted record(s) for '{} {}'", firstName, lastName);
        } else {
            log.error("deletePerson: no record found for '{} {}'", firstName, lastName);
        }

        return personRemoved;
    }
}