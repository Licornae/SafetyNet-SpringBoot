package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonInfoDTO;
import com.safetynet.alerts.exception.DataNotLoadedException;
import com.safetynet.alerts.exception.PersonNotFoundException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Default implementation of PersonInfoService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonInfoServiceImpl implements PersonInfoService {

    private final DataRepository dataRepository;

    private final PersonMedicalInfo personMedicalInfo;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PersonInfoDTO> getPersonInfoByLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            log.error("getPersonInfoByLastName called with blank lastName");
            throw new IllegalArgumentException("lastName must not be blank");
        }

        DataContainer dataContainer = dataRepository.getDataContainer();
        if (dataContainer == null) {
            log.error("DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        List<PersonInfoDTO> result = dataContainer.getPersons().stream()
                .filter(Objects::nonNull)
                .filter(person -> person.getLastName() != null && person.getLastName().equals(lastName))
                .map(this::toDto)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            log.error("No persons found for lastName={}", lastName);
            throw new PersonNotFoundException("Person not found");
        }
        log.debug("Found {} person(s) for lastName={}", result.size(), lastName);

        return result;
    }

    private PersonInfoDTO toDto(Person person) {
        Integer age = personMedicalInfo.getAgeFor(person);

        List<String> medications = personMedicalInfo.getMedicationsFor(person);
        if (medications == null) {
            medications = Collections.emptyList();
        }

        List<String> allergies = personMedicalInfo.getAllergiesFor(person);
        if (allergies == null) {
            allergies = Collections.emptyList();
        }

        return new PersonInfoDTO(
                person.getFirstName(),
                person.getLastName(),
                person.getAddress(),
                age,
                person.getEmail(),
                medications,
                allergies
        );
    }

}
