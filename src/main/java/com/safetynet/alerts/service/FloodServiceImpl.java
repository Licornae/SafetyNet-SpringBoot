package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FloodHouseholdDTO;
import com.safetynet.alerts.dto.PersonMedicalInfoDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.exception.DataNotLoadedException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FloodServiceImpl implements FloodService {

    private final DataRepository dataRepository;

    private final MedicalInfoService medicalInfoService;

    @Override
    public List<FloodHouseholdDTO> getHouseholdsByStations(List<String> stations) {
        if (stations == null || stations.isEmpty() || stations.stream().allMatch(station -> station == null || station.isBlank())) {
            throw new IllegalArgumentException("stations must not be blank");
        }

        DataContainer dataContainer = dataRepository.getDataContainer();

        if (dataContainer == null) {
            log.error("DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        //Récupérer les adresses couvertes par la ou les station(s)
        Set<String> coveredAddresses = new LinkedHashSet<>();
        Map<String, String> addressToStation = new LinkedHashMap<>();

        for (FireStation fireStation : dataContainer.getFirestations()) {
            if (fireStation == null) continue;
            if (stations.contains(fireStation.getStation())) {
                coveredAddresses.add(fireStation.getAddress());
                addressToStation.putIfAbsent(fireStation.getAddress(), fireStation.getStation());
            }
        }

        if (coveredAddresses.isEmpty()) {
            log.info("No addresses found for stations {}", stations);
            throw new AddressNotFoundException("No households found for given stations");
        }

        //Grouper les personnes par adresses couvertes
        Map<String, List<Person>> personsByAddress = new LinkedHashMap<>();

        for (String address : coveredAddresses) {
            personsByAddress.put(address, new ArrayList<>());
        }

        dataContainer.getPersons().stream()
                .filter(Objects::nonNull)
                .forEach(person -> {
                    String address = person.getAddress();
                    if (coveredAddresses.contains(address)) {
                        personsByAddress.get(address).add(person);
                    }
                });

        if (personsByAddress.isEmpty()) {
            log.info("No residents found at covered addresses {}", coveredAddresses);
            throw new AddressNotFoundException("No residents found for given stations");
        }

        //Construire le retour
        List<FloodHouseholdDTO> households = new ArrayList<>();
        for (String address : coveredAddresses) {
            List<Person> persons = personsByAddress.getOrDefault(address, List.of());
            if (persons.isEmpty()) continue;

            List<PersonMedicalInfoDTO> residents = new ArrayList<>(persons.size());
            for (Person person : persons) {
                Integer age = medicalInfoService.getAgeFor(person);
                List<String> medications = medicalInfoService.getMedicationsFor(person);
                List<String> allergies = medicalInfoService.getAllergiesFor(person);

                residents.add(new PersonMedicalInfoDTO(
                        person.getFirstName(),
                        person.getLastName(),
                        person.getPhone(),
                        age,
                        medications,
                        allergies
                ));
            }

            String station = addressToStation.get(address);
            households.add(new FloodHouseholdDTO(station, address, residents));
        }
        return households;
    }
}
