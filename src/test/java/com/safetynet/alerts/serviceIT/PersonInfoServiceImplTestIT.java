package com.safetynet.alerts.serviceIT;

import com.safetynet.alerts.dto.PersonInfoDTO;
import com.safetynet.alerts.exception.PersonNotFoundException;
import com.safetynet.alerts.repository.DataRepository;
import com.safetynet.alerts.service.PersonInfoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class PersonInfoServiceImplTestIT {

    @Autowired
    private PersonInfoServiceImpl personInfoService;

    @Autowired
    DataRepository dataRepository;

    @BeforeEach
    public void resetData() {
        dataRepository.reloadData();
    }

    @Test
    public void testGetPersonInfo_ExistingLastName_ReturnsPersonsWithAllInfo() {

        String lastName = "Boyd";

        List<PersonInfoDTO> persons = personInfoService.getPersonInfoByLastName(lastName);

        assertNotNull(persons, "The list must not be empty");
        assertFalse(persons.isEmpty(), "The list must not be empty for an existing name");
        assertEquals(6, persons.size(), "There are 5 Boyd");

        PersonInfoDTO john = persons.stream()
                .filter(personInfoDTO -> "John".equalsIgnoreCase(personInfoDTO.getFirstName()))
                .findFirst()
                .orElse(null);

        assertNotNull(john, "John Boyd should exist");
        assertEquals("Boyd", john.getLastName(), "The last name is Boyd");
        assertNotNull(john.getAddress(), "There is an address");
        assertNotNull(john.getEmail(), "There is an email");
        assertNotNull(john.getMedications(), "There are medications");
        assertNotNull(john.getAllergies(), "There are allergies");
    }

    @Test
    public void testGetPersonInfo_UnknownLastName_ThrowsPersonNotFound() {
        assertThrows(PersonNotFoundException.class,
                () -> personInfoService.getPersonInfoByLastName("Unknown"));
    }

}