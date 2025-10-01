package com.safetynet.alerts.service;


import com.safetynet.alerts.dto.ChildAlertDTO;
import com.safetynet.alerts.dto.FamilyMembersDTO;
import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildAlertServiceImpl implements ChildAlertService {

    private final PeopleWithAgeService peopleWithAgeService;

    @Override
    public List<ChildAlertDTO> getChildrenByAddress(String address) throws AddressNotFoundException {

        if (address == null || address.isBlank()) {
            throw new AddressNotFoundException("address is required");
        }

        //Toutes les personnes vivant à l'adresse donnée
        List<PersonDTO> residents = peopleWithAgeService.listAll().stream()
                .filter(Objects::nonNull)
                .filter(personDTO -> address.equals(personDTO.getAddress()))
                .toList();

        //Liste des enfants vivant à l'adresse
        List<PersonDTO> children = residents.stream()
                .filter(personDTO -> personDTO.getAge() <= 18)
                .toList();

        //Pour chaque enfant déterminer les FamilyMembers
        return children.stream()
                .map(child -> new ChildAlertDTO(
                        child.getFirstName(),
                        child.getLastName(),
                        child.getAge(),
                        residents.stream()
                                // on enlève l’instance de l’enfant
                                .filter(personDTO -> personDTO != child)
                                .map(personDTO -> new FamilyMembersDTO(personDTO.getFirstName(), personDTO.getLastName(), ageCategoryOf(personDTO)))
                                .toList()
                ))
                .toList();
    }

    private static String ageCategoryOf(PersonDTO personDTO) {
        Integer age = personDTO.getAge();
        return age <= 18 ? "Child" : "Adult";
    }
}
