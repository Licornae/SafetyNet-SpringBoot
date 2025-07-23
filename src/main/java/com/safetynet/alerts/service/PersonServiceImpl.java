
package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

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
}