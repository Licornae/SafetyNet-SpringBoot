package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FireDTO;
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

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class FireServiceImpl implements FireService {

    private final DataRepository dataRepository;
    private final MedicalInfoService medicalInfoService;

    @Override
    public FireDTO getResidentsByAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("address must not be blank");
        }

        DataContainer dataContainer = dataRepository.getDataContainer();

        if (dataContainer == null) {
            log.error("DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        FireStation fireStation = dataContainer.getFirestations().stream()
                .filter(fireStation1 -> address.equals(fireStation1.getAddress()))
                .findFirst()
                .orElse(null);

        if (fireStation == null) {
            throw new AddressNotFoundException("No station for address " + address);
        }

        List<Person> personsAtAddress = dataContainer.getPersons().stream()
                .filter(person -> address.trim().equals(person.getAddress().trim()))
                .toList();

        if (personsAtAddress.isEmpty()) {
            throw new AddressNotFoundException("No residents at address " + address);
        }

        List<PersonMedicalInfoDTO> residents = new ArrayList<>();
        for (Person person : personsAtAddress) {
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
        return new FireDTO(fireStation.getStation(),residents);
    }
}
