package com.safetynet.alerts.serviceIT;

import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PeopleWithAgeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import com.safetynet.alerts.repository.DataRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PeopleWithAgeServiceImplTestIT {

    @Autowired
    private PeopleWithAgeService peopleWithAgeService;

    @Autowired
    private DataRepository dataRepository;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @BeforeEach
    public void resetData() {
        dataRepository.reloadData();
    }

    @Test
    public void listAll_fieldsAreMapped_AndAgeComputedForKnownPerson() {

        DataContainer dataContainer = dataRepository.getDataContainer();
        assertNotNull(dataContainer);

        Person john = dataContainer.getPersons().stream()
                .filter(person -> person != null
                        && "John".equals(person.getFirstName())
                        && "Boyd".equals(person.getLastName()))
                .findFirst()
                .orElse(null);
        assertNotNull(john, "John Boyd should exist");

        MedicalRecord johnMr = dataContainer.getMedicalrecords().stream()
                .filter(mr -> mr != null
                        && "John".equals(mr.getFirstName())
                        && "Boyd".equals(mr.getLastName()))
                .findFirst()
                .orElse(null);
        assertNotNull(johnMr, "The John's medical record should exist");

        LocalDate birth = LocalDate.parse(johnMr.getBirthdate(), FORMAT);
        int expectedAge = Period.between(birth, LocalDate.now()).getYears();

        List<PersonDTO> result = peopleWithAgeService.listAll();

        PersonDTO johnDto = result.stream()
                .filter(dto -> "John".equals(dto.getFirstName()) && "Boyd".equals(dto.getLastName()))
                .findFirst()
                .orElse(null);

        assertNotNull(johnDto);
        assertEquals(expectedAge, johnDto.getAge());
        assertEquals(john.getAddress(), johnDto.getAddress());
        assertEquals(john.getPhone(), johnDto.getPhone());
    }

    @Test
    public void listAll_ReturnsAllPersons_AndNotEmpty() {
        DataContainer dataContainer = dataRepository.getDataContainer();
        assertNotNull(dataContainer, "dataContainer should load");
        int expectedSize = dataContainer.getPersons() != null ? dataContainer.getPersons().size() : 0;

        List<PersonDTO> result = peopleWithAgeService.listAll();

        assertNotNull(result);
        assertFalse(result.isEmpty(), "the list must not be empty");
        assertEquals(expectedSize, result.size(), "the repo size should be the same");
    }
}
