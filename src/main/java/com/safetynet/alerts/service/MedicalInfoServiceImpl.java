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

/**
 * Default implementation of MedicalInfoService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalInfoServiceImpl implements MedicalInfoService {

    private final DataRepository dataRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public MedicalRecord findMedicalRecordByName(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            log.warn("findMedicalRecordByName called with null parameter(s): firstName={}, lastName={}", firstName, lastName);
            return null;
        }

        DataContainer dataContainer = dataRepository.getDataContainer();
        if (dataContainer == null) {
            log.error("findMedicalRecordByName: DataContainer not loaded");
            return null;
        }
        if (dataContainer.getMedicalrecords() == null) {
            log.debug("findMedicalRecordByName: medicalrecords list is null in DataContainer");
            return null;
        }

        for (MedicalRecord medicalRecord : dataContainer.getMedicalrecords()) {
            if (medicalRecord == null || medicalRecord.getFirstName() == null || medicalRecord.getLastName() == null) continue;
            if (firstName.equals(medicalRecord.getFirstName()) && lastName.equals(medicalRecord.getLastName())) {
                log.trace("findMedicalRecordByName: match found for {} {}", firstName, lastName);
                return medicalRecord;
            }
        }
        log.debug("findMedicalRecordByName: no record found for {} {}", firstName, lastName);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getAgeFor(Person person) {
        if (person == null) {
            log.warn("getAgeFor called with null person");
            return null;
        }

        MedicalRecord medicalRecord = findMedicalRecordByName(person.getFirstName(), person.getLastName());
        if (medicalRecord == null) {
            log.debug("getAgeFor: no medical record for {} {}", person.getFirstName(), person.getLastName());
            return null;
        }

        try {
            Integer age = AgeCalculator.computeAge(medicalRecord.getBirthdate());
            log.trace("getAgeFor: computed age={} for {} {}", age, person.getFirstName(), person.getLastName());

            return age;

        } catch (Exception e) {
            log.debug("getAgeFor: invalid birthdate for {} {}: {}", person.getFirstName(), person.getLastName(), e.getMessage());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMedicationsFor(Person person) {
        if (person == null) {
            log.warn("getMedicationsFor called with null person");
            return Collections.emptyList();
        }

        MedicalRecord medicalRecord = findMedicalRecordByName(person.getFirstName(), person.getLastName());
        if (medicalRecord == null) {
            log.debug("getMedicationsFor: no medical record for {} {}", person.getFirstName(), person.getLastName());
            return Collections.emptyList();
        }

        List<String> medications = medicalRecord.getMedications();
        if (medications == null) {
            log.debug("getMedicationsFor: medications list is null for {} {}", person.getFirstName(), person.getLastName());
            return Collections.emptyList();
        }

        log.trace("getMedicationsFor: {} medication(s) for {} {}", medications.size(), person.getFirstName(), person.getLastName());
        return medications;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllergiesFor(Person person) {
        if (person == null) {
            log.warn("getAllergiesFor called with null person");
            return Collections.emptyList();
        }

        MedicalRecord medicalRecord = findMedicalRecordByName(person.getFirstName(), person.getLastName());
        if (medicalRecord == null) {
            log.debug("getAllergiesFor: no medical record for {} {}", person.getFirstName(), person.getLastName());
            return Collections.emptyList();
        }

        List<String> allergies = medicalRecord.getAllergies();
        if (allergies == null) {
            log.debug("getAllergiesFor: allergies list is null for {} {}", person.getFirstName(), person.getLastName());
            return Collections.emptyList();
        }

        log.trace("getAllergiesFor: {} allergies for {} {}", allergies.size(), person.getFirstName(), person.getLastName());
        return allergies;
    }
}
