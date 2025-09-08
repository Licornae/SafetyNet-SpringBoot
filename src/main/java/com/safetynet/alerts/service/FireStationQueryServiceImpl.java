package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FirestationCoverageDTO;
import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.exception.DataNotLoadedException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import com.safetynet.alerts.util.AgeCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class FireStationQueryServiceImpl implements FireStationQueryService {

    private final DataRepository dataRepository;

    @Override
    public FirestationCoverageDTO getCoverageByStation(String stationNumber) {

        if (stationNumber == null || stationNumber.isBlank()) {
            throw new IllegalArgumentException("stationNumber is required");
        }

        DataContainer dataContainer = dataRepository.getDataContainer();

        if (dataContainer == null) {
            log.error("DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        //Adresses couvertes par la station demandée
        Set<String> coveredAddresses = dataContainer.getFirestations().stream()
                .filter(firestation -> stationNumber.equals(firestation.getStation()))
                .map(FireStation::getAddress)
                .collect(Collectors.toSet());

        if (coveredAddresses.isEmpty()) {
            log.info("No firestation mapping found for station {}", stationNumber);
            throw new AddressNotFoundException("No address found for this station");
        }

        // Personnes couvertes par la station
        List<PersonDTO> persons = new ArrayList<>();
        int adults = 0;
        int children = 0;

        for (Person person : dataContainer.getPersons()) {
            if (person == null) continue;
            if (!coveredAddresses.contains(person.getAddress())) continue;

            //Calculer Age
            MedicalRecord medicalRecord = findMedicalRecordByName(dataContainer.getMedicalrecords(),
                    person.getFirstName(), person.getLastName());
            if (medicalRecord == null) continue;

            //Compter enfant et adultes
            int age = AgeCalculator.computeAge(medicalRecord.getBirthdate());
            if (age <= 18) children++;
            else adults++;

            persons.add(new PersonDTO(
                    person.getFirstName(),
                    person.getLastName(),
                    age,
                    person.getAddress(),
                    person.getPhone()
            ));
        }
        return new FirestationCoverageDTO(Integer.valueOf(stationNumber), persons, adults, children);
    }

    //recherche du dossier médical correspondant (prénom + nom)
    private static MedicalRecord findMedicalRecordByName(List<MedicalRecord> list, String firstName, String lastName) {
        if (list == null || firstName == null || lastName == null) return null;
        String first = firstName.trim();
        String last = lastName.trim();
        for (MedicalRecord medicalRecord : list) {
            if (medicalRecord == null) continue;
            if (first.equals(medicalRecord.getFirstName()) && last.equals(medicalRecord.getLastName())) {
                return medicalRecord; // premier match
            }
        }
        return null;
    }
}
