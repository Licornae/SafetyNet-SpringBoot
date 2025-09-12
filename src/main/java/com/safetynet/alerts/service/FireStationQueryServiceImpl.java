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
    private final PeopleWithAgeService peopleWithAgeService;

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

        List<PersonDTO> allPeopleWithAge = peopleWithAgeService.listAll();

        // 2) Filtrer par adresses couvertes + ignorer ceux sans âge
        List<PersonDTO> persons = new ArrayList<>();
        int adults = 0;
        int children = 0;

        for (PersonDTO personDTO : allPeopleWithAge) {
            if (personDTO == null) continue;
            if (!coveredAddresses.contains(personDTO.getAddress())) continue;

            if (personDTO.getAge() <= 18) children++;
            else adults++;

            persons.add(personDTO);
        }
        return new FirestationCoverageDTO(Integer.valueOf(stationNumber), persons, adults, children);
    }
}
