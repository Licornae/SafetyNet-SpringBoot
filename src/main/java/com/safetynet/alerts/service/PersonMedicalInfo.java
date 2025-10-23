package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;

import java.util.List;

public interface PersonMedicalInfo {

    MedicalRecord findMedicalRecordByName(String firstName, String lastName);
    Integer getAgeFor(Person person);
    List<String> getMedicationsFor(Person person);
    List<String> getAllergiesFor(Person person);

    // Ex-PeopleWithAgeService
    List<PersonDTO> listAllPeopleWithAge();
}
