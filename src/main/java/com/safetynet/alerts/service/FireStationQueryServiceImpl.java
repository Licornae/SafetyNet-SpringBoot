package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FirestationCoverageDTO;
import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.exception.DataNotLoadedException;
import com.safetynet.alerts.exception.StationNotFoundException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default implementation of FireStationQueryService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FireStationQueryServiceImpl implements FireStationQueryService {

    private final DataRepository dataRepository;
    private final PeopleWithAgeService peopleWithAgeService;

    /**
     * {@inheritDoc}
     */
    @Override
    public FirestationCoverageDTO getCoverageByStation(String stationNumber) {
        log.info("getCoverageByStation called with stationNumber='{}'", stationNumber);

        if (stationNumber == null || stationNumber.isBlank()) {
            log.warn("getCoverageByStation: blank stationNumber");
            throw new IllegalArgumentException("stationNumber is required");
        }

        DataContainer dataContainer = dataRepository.getDataContainer();

        if (dataContainer == null) {
            log.error("DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        boolean stationExists = dataContainer.getFirestations().stream()
                .filter(fireStation -> fireStation != null && fireStation.getStation() != null)
                .anyMatch(fireStation -> stationNumber.equals(fireStation.getStation()));

        if (!stationExists) {
            log.warn("Station '{}' doesn't exist", stationNumber);
            throw new StationNotFoundException("Station " + stationNumber + " not found");
        }

        //Addresses covered by the requested station
        Set<String> coveredAddresses = dataContainer.getFirestations().stream()
                .filter(firestation -> stationNumber.equals(firestation.getStation()))
                .map(FireStation::getAddress)
                .collect(Collectors.toSet());

        if (coveredAddresses.isEmpty()) {
            log.warn("No firestation mapping found for station {}", stationNumber);
            throw new AddressNotFoundException("No address found for station " + stationNumber);
        }

        List<PersonDTO> allPeopleWithAge = peopleWithAgeService.listAll();

        //Filter persons by covered addresses and count adults/children
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

        log.info("getCoverageByStation('{}'): persons={}, adults={}, children={}", stationNumber, persons.size(), adults, children);

        return new FirestationCoverageDTO(Integer.parseInt(stationNumber), persons, adults, children);
    }
}
