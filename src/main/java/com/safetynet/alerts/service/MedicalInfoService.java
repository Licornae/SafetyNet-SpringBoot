package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;

import java.util.List;

public interface MedicalInfoService {
    MedicalRecord findMedicalRecordByName(String firstName, String lastName);
    Integer getAgeFor(Person person);
    List<String> getMedicationsFor(Person person);
    List<String> getAllergiesFor(Person person);
}
