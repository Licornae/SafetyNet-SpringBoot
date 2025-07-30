
package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.PersonNotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    private final DataRepository dataRepository;

    @Autowired
    public PersonServiceImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }
    @Override
    public Person getPerson(String firstName, String lastName) {
        return dataRepository.getDataContainer().getPersons().stream()
                .filter(p -> p.getFirstName().equals(firstName)
                        && p.getLastName().equals(lastName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Person addPerson(Person person) {
        dataRepository.getDataContainer().getPersons().add(person);
        return person;
    }

    @Override
    public Person updatePerson(String firstName, String lastName, Person updatedPerson) {
        List<Person> persons = dataRepository.getDataContainer().getPersons();

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
        throw new PersonNotFoundException("Person not found: " + firstName + " " + lastName);
    }
}