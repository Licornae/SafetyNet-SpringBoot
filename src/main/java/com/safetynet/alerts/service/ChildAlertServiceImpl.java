package com.safetynet.alerts.service;


import com.safetynet.alerts.dto.ChildAlertDTO;
import com.safetynet.alerts.dto.FamilyMembersDTO;
import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;

import com.safetynet.alerts.repository.DataRepository;
import com.safetynet.alerts.util.AddressNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.safetynet.alerts.util.AddressNormalizer.equalsNormalized;

/**
 * Default implementation of ChildAlertService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChildAlertServiceImpl implements ChildAlertService {

    private final PeopleWithAgeService peopleWithAgeService;
    private final DataRepository dataRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChildAlertDTO> getChildrenByAddress(String address) throws AddressNotFoundException {

        String normAddress = AddressNormalizer.normalize(address);

        if (normAddress == null || normAddress.isBlank()) {
            log.warn("getChildrenByAddress called with blank address");
            throw new IllegalArgumentException("address is required");
        }

        boolean addressExists = peopleWithAgeService.listAll().stream()
                .filter(Objects::nonNull)
                .map(PersonDTO::getAddress)
                .filter(Objects::nonNull)
                .anyMatch(a-> equalsNormalized(a, normAddress));

        if (!addressExists) {
            log.warn("Address not found for input='{}'", address);
            throw new AddressNotFoundException("Address not found");
        }

        // All residents living at the given address
        List<PersonDTO> residents = peopleWithAgeService.listAll().stream()
                .filter(Objects::nonNull)
                .filter(personDTO -> equalsNormalized(personDTO.getAddress(), normAddress))
                .toList();

        log.debug("Found {} resident(s) at address='{}'", residents.size(), address);

        //List of children living at the address
        List<PersonDTO> children = residents.stream()
                .filter(personDTO -> personDTO.getAge() <= 18)
                .toList();

        log.debug("Children at address='{}': {}", address, children.size());

        // For each child, determine family members
        return children.stream()
                .map(child -> new ChildAlertDTO(
                        child.getFirstName(),
                        child.getLastName(),
                        child.getAge(),
                        residents.stream()
                                .filter(personDTO -> personDTO != child) // exclude the child itself
                                .map(personDTO -> new FamilyMembersDTO(personDTO.getFirstName(), personDTO.getLastName(), ageCategoryOf(personDTO)))
                                .toList()
                ))
                .toList();
    }

    /**
     * Determines the age category of a person.
     *
     * @param personDTO person to categorize
     * @return "Child" when age <= 18, otherwise "Adult"
     */
    private static String ageCategoryOf(PersonDTO personDTO) {
        int age = personDTO.getAge();
        return age <= 18 ? "Child" : "Adult"; //Ternary: returns "Child" when age <= 18, otherwise "Adult"
    }
}
