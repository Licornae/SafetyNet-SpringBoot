package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import org.springframework.stereotype.Service;

public interface PersonService {
    Person getPerson(String firstName, String lastName);
}