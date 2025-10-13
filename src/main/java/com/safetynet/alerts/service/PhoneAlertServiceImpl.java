package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.StationNotFoundException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.safetynet.alerts.dto.PersonDTO;

/**
 * Default implementation of PhoneAlertService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneAlertServiceImpl implements PhoneAlertService {

    private final DataRepository dataRepository;
    private final PeopleWithAgeService peopleWithAgeService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getPhonesByFirestation(String stationNumber) {
        log.debug("phoneAlert.getPhonesByFirestation called, station='{}'", stationNumber);

        if (stationNumber == null || stationNumber.isBlank()) {
            log.error("phoneAlert: blank or null station parameter");
            throw new IllegalArgumentException("firestation parameter is required");
        }

        DataContainer dataContainer = dataRepository.getDataContainer();

        //Check station existence
        boolean stationExists = dataContainer.getFirestations().stream()
                .filter(Objects::nonNull)
                .anyMatch(fireStation -> stationNumber.equals(fireStation.getStation()));

        if (!stationExists) {
            log.error("Station {} not found", stationNumber);
            throw new StationNotFoundException("This station doesn't exist");
        }

        //Addresses covered by the requested station
        Set<String> coveredAddresses = dataContainer.getFirestations().stream()
                .filter(firestation -> stationNumber.equals(firestation.getStation()))
                .map(FireStation::getAddress)
                .collect(Collectors.toSet());

        if (coveredAddresses.isEmpty()) {
            log.error("No addresses covered by the station {}", stationNumber);
            return List.of();
        }

        //Distinct phone numbers at covered addresses
        List<PersonDTO> allPeople = peopleWithAgeService.listAll();
        Set<String> uniquePhones = new HashSet<>();

        for (PersonDTO personDTO : allPeople) {
            if (personDTO == null) continue;
            if (!coveredAddresses.contains(personDTO.getAddress())) continue;
            if (personDTO.getPhone() == null || personDTO.getPhone().isBlank()) continue;
            uniquePhones.add(personDTO.getPhone().trim());
        }
        log.debug("phoneAlert: returning {} phone(s) for station '{}'", uniquePhones.size(), stationNumber);

        return uniquePhones.stream().toList();
    }

}
