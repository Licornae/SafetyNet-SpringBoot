package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import com.safetynet.alerts.util.AgeCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeopleWithAgeServiceImpl implements PeopleWithAgeService {

    private final DataRepository dataRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PersonDTO> listAll() {

        DataContainer dataContainer = dataRepository.getDataContainer();

        if(dataContainer == null){
            log.warn("Data container is null, returning empty list");
            return List.of();
        }

        List<PersonDTO> people = new ArrayList<>();
        List<MedicalRecord> medicalRecords = dataContainer.getMedicalrecords();

        for (Person person : dataContainer.getPersons()) {
            if (person == null) continue;

            MedicalRecord mr = findMedicalRecordByName(medicalRecords, person.getFirstName(), person.getLastName());
            Integer age = null;
            if (mr != null) {
                try {
                    age = AgeCalculator.computeAge(mr.getBirthdate());
                    log.trace("Computed age={} for {} {}", age, person.getFirstName(), person.getLastName());
                } catch (Exception e) {
                    log.debug("Invalid birthdate for {} {}: {}", person.getFirstName(), person.getLastName(), e.getMessage());
                }
            }

            int safeAge = (age != null) ? age : -1; //-1 when unknown age

            people.add(new PersonDTO(
                    person.getFirstName(),
                    person.getLastName(),
                    safeAge,
                    person.getAddress(),
                    person.getPhone()
            ));
        }

        log.debug("PeopleWithAgeServiceImpl.listAll() completed: built {} PersonDTO(s)", people.size());
        return people;
    }

    /**
     * Find the MedicalRecord matching for the given first and last name.
     * Returns null when inputs are null or no record matches.
     */
    private static MedicalRecord findMedicalRecordByName(List<MedicalRecord> list, String firstName, String lastName) {
        if (list == null || firstName == null || lastName == null) return null;
        String first = firstName.trim();
        String last = lastName.trim();
        for (MedicalRecord medicalRecord : list) {
            if (medicalRecord == null) continue;
            if (first.equals(medicalRecord.getFirstName()) && last.equals(medicalRecord.getLastName())) {
                return medicalRecord;
            }
        }
        return null;
    }
}
