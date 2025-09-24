package com.safetynet.alerts.service;

import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.util.AgeCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.safetynet.alerts.repository.DataRepository;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalInfoServiceImpl implements MedicalInfoService {

    private final DataRepository dataRepository;

    @Override
    public MedicalRecord findMedicalRecordByName(String firstName, String lastName) {

        DataContainer dataContainer = dataRepository.getDataContainer();
        if (dataContainer == null || dataContainer.getMedicalrecords() == null || firstName == null || lastName == null) return null;

        for (MedicalRecord medicalRecord : dataContainer.getMedicalrecords()) {
            if (medicalRecord == null || medicalRecord.getFirstName() == null || medicalRecord.getLastName() == null) continue;
            if (firstName.equals(medicalRecord.getFirstName()) && lastName.equals(medicalRecord.getLastName())) {
                return medicalRecord;
            }
        }
        return null;
    }

    @Override
    public Integer getAgeFor(Person person) {
        if (person == null) return null;
        MedicalRecord medicalRecord = findMedicalRecordByName(person.getFirstName(), person.getLastName());
        if (medicalRecord == null) return null;
        try {
            return AgeCalculator.computeAge(medicalRecord.getBirthdate());
        } catch (Exception e) {
            log.debug("Invalid birthdate for {} {}: {}", person.getFirstName(), person.getLastName(), e.getMessage());
            return null;
        }
    }

    @Override
    public List<String> getMedicationsFor(Person person) {
        if (person == null) {
            return Collections.emptyList();
        }

        MedicalRecord medicalRecord = findMedicalRecordByName(
                person.getFirstName(),
                person.getLastName()
        );

        if (medicalRecord == null) {
            return Collections.emptyList();
        }

        List<String> medications = medicalRecord.getMedications();
        if (medications == null) {
            return Collections.emptyList();
        }

        return medications;
    }

    @Override
    public List<String> getAllergiesFor(Person person) {
        if (person == null) {
            return Collections.emptyList();
        }

        MedicalRecord medicalRecord = findMedicalRecordByName(
                person.getFirstName(),
                person.getLastName()
        );

        if (medicalRecord == null) {
            return Collections.emptyList();
        }

        List<String> allergies = medicalRecord.getAllergies();
        if (allergies == null) {
            return Collections.emptyList();
        }

        return allergies;
    }
}