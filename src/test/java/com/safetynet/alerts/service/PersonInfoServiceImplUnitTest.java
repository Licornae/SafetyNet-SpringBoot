package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.dto.PersonInfoDTO;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
import java.util.List;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonInfoServiceImplUnitTest {

    @Mock
    DataRepository dataRepository;

    @Mock
    PeopleWithAgeService peopleWithAgeService;

    @InjectMocks
    PersonInfoServiceImpl service;

    private DataContainer container;

    private static PersonInfoDTO find(List<PersonInfoDTO> list, String firstName) {
        return list.stream().filter(personInfoDTO -> firstName.equals(personInfoDTO.getFirstName()))
                .findFirst().orElseThrow(() -> new AssertionError("Not found: " + firstName));
    }

    @BeforeEach
    public void setUp() {

        List<Person> persons = new ArrayList<>(of(
                new Person("John",   "Boyd",   "1509 Culver St", "Culver", "97451", "841-874-6512", "john.boyd@email.com"),
                new Person("Jacob",  "Boyd",   "1509 Culver St", "Culver", "97451", "841-874-6513", "jacob.boyd@email.com"),
                new Person("Tenley", "Boyd",   "1509 Culver St", "Culver", "97451", "841-874-6512", "tenley.boyd@email.com"),
                new Person("Peter",  "Duncan", "644 Gershwin Cir", "Culver", "97451", "841-874-7458", "peter.duncan@email.com")
        ));

        List<MedicalRecord> medicals = new ArrayList<>(of(
                new MedicalRecord("John",   "Boyd",   "1984-03-06", of("aznol:200mg", "hydrapermazol:100mg"), of("nillacilan")),
                new MedicalRecord("Jacob",  "Boyd",   "1988-01-03", of("pharmacol:5000mg"), of()),
                new MedicalRecord("Tenley", "Boyd",   "2013-02-18", of("ibupurin:200mg"), of("peanut")),
                new MedicalRecord("Peter",  "Duncan", "2005-09-06", of(), of())
        ));

        container = new DataContainer();
        container.setPersons(persons);
        container.setMedicalrecords(medicals);

        when(dataRepository.getDataContainer()).thenReturn(container);
    }

    @Test
    public void getPersonInfo_LastNameBoyds_ReturnsAllBoyds() {

        when(peopleWithAgeService.listAll()).thenReturn(of(
                new PersonDTO("John",   "Boyd", 40, "1509 Culver St", "841-874-6512"),
                new PersonDTO("Jacob",  "Boyd", 36, "1509 Culver St", "841-874-6513"),
                new PersonDTO("Tenley", "Boyd", 12, "1509 Culver St", "841-874-6512"),
                new PersonDTO("Peter",  "Duncan", 20, "644 Gershwin Cir", "841-874-7458")
        ));

        List<PersonInfoDTO> result = service.getPersonInfoByLastName("Boyd");

        assertNotNull(result);
        assertEquals(3, result.size(), "Three Boyds expected");

        PersonInfoDTO john = find(result, "John");
        assertEquals("Boyd", john.getLastName());
        assertEquals(40, john.getAge());
        assertEquals("1509 Culver St", john.getAddress());
        assertEquals("john.boyd@email.com", john.getEmail());
        assertTrue(john.getMedications().contains("aznol:200mg"));
        assertTrue(john.getAllergies().contains("nillacilan"));

        PersonInfoDTO jacob = find(result, "Jacob");
        assertEquals(36, jacob.getAge());
        assertEquals("jacob.boyd@email.com", jacob.getEmail());
        assertTrue(jacob.getMedications().contains("pharmacol:5000mg"));
        assertTrue(jacob.getAllergies().isEmpty());

        PersonInfoDTO tenley = find(result, "Tenley");
        assertEquals(12, tenley.getAge());
        assertEquals("tenley.boyd@email.com", tenley.getEmail());
        assertTrue(tenley.getMedications().contains("ibupurin:200mg"));
        assertTrue(tenley.getAllergies().contains("peanut"));
    }

    @Test
    public void getPersonInfo_UnknownLastName_ReturnsEmpty() {
        when(peopleWithAgeService.listAll()).thenReturn(of(
                new PersonDTO("Peter", "Duncan", 20, "644 Gershwin Cir", "841-874-7458")
        ));

        List<PersonInfoDTO> result = service.getPersonInfoByLastName("Unknown");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}