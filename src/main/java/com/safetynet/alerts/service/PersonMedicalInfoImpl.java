package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.safetynet.alerts.util.AgeCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonMedicalInfoImpl implements PersonMedicalInfo {

    private final DataRepository dataRepository;

    @Override
    public MedicalRecord findMedicalRecordByName(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            log.debug("findMedicalRecordByName called with null parameter(s): firstName={}, lastName={}", firstName, lastName);
            return null;
        }

        DataContainer dataContainer = dataRepository.getDataContainer();
        if (dataContainer == null || dataContainer.getMedicalrecords() == null) {
            log.error("findMedicalRecordByName: DataContainer/medicalrecords not loaded");
            return null;
        }
        for (MedicalRecord medicalRecord : dataContainer.getMedicalrecords()) {
            if (medicalRecord == null || medicalRecord.getFirstName() == null || medicalRecord.getLastName() == null) continue;
            if (firstName.equals(medicalRecord.getFirstName()) && lastName.equals(medicalRecord.getLastName())) {
                log.debug("findMedicalRecordByName: match found for {} {}", firstName, lastName);
                return medicalRecord;
            }
        }
        log.debug("findMedicalRecordByName: no record found for {} {}", firstName, lastName);
        return null;
    }

    @Override
    public Integer getAgeFor(Person person) {
        if (person == null) {
            log.debug("getAgeFor called with null person");
            return null;
        }

        MedicalRecord medicalRecord = findMedicalRecordByName(person.getFirstName(), person.getLastName());
        if (medicalRecord == null)
            return null;

        try {
            Integer age = AgeCalculator.computeAge(medicalRecord.getBirthdate());
            log.debug("getAgeFor: computed age={} for {} {}", age, person.getFirstName(), person.getLastName());
            return age;

        } catch (Exception e) {
            log.error("getAgeFor: invalid birthdate for {} {}: {}", person.getFirstName(), person.getLastName(), e.getMessage());
            return null;
        }
    }

    @Override
    public List<String> getMedicationsFor(Person person) {
        if (person == null) return Collections.emptyList();

        MedicalRecord medicalRecord = findMedicalRecordByName(person.getFirstName(), person.getLastName());
        return medicalRecord != null && medicalRecord.getMedications() != null ? medicalRecord.getMedications() : Collections.emptyList();
    }

    @Override
    public List<String> getAllergiesFor(Person person) {
        if (person == null) return Collections.emptyList();

        MedicalRecord medicalRecord = findMedicalRecordByName(person.getFirstName(), person.getLastName());
        return medicalRecord != null && medicalRecord.getAllergies() != null ? medicalRecord.getAllergies() : Collections.emptyList();
    }

    @Override
    public List<PersonDTO> listAllPeopleWithAge() {

        DataContainer dataContainer = dataRepository.getDataContainer();
        if (dataContainer == null) {
            log.error("listAllPeopleWithAge: Data container is null, returning empty list");
            return List.of();
        }

        List<PersonDTO> people = new ArrayList<>();
        for (Person person : dataContainer.getPersons()) {
            if (person == null) continue;

            Integer age = getAgeFor(person); // centralised
            int safeAge = (age != null) ? age : -1; //-1 when unknown age

            people.add(new PersonDTO(
                    person.getFirstName(),
                    person.getLastName(),
                    safeAge,
                    person.getAddress(),
                    person.getPhone()
            ));
        }
        log.debug("listAllPeopleWithAge completed: built {} PersonDTO(s)", people.size());
        return people;
    }
}
