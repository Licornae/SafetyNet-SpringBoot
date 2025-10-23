package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonInfoDTO;
import com.safetynet.alerts.exception.PersonNotFoundException;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonInfoServiceImplUnitTest {

    @Mock
    DataRepository dataRepository;

    @Mock
    PersonMedicalInfo personMedicalInfo;

    @InjectMocks
    PersonInfoServiceImpl service;

    private DataContainer container;

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
        Person john   = container.getPersons().get(0);
        Person jacob  = container.getPersons().get(1);
        Person tenley = container.getPersons().get(2);

        when(personMedicalInfo.getAgeFor(john)).thenReturn(40);
        when(personMedicalInfo.getAgeFor(jacob)).thenReturn(36);
        when(personMedicalInfo.getAgeFor(tenley)).thenReturn(12);

        when(personMedicalInfo.getMedicationsFor(john)).thenReturn(of("aznol:200mg"));
        when(personMedicalInfo.getAllergiesFor(john)).thenReturn(of("nillacilan"));

        when(personMedicalInfo.getMedicationsFor(jacob)).thenReturn(of("pharmacol:5000mg"));
        when(personMedicalInfo.getAllergiesFor(jacob)).thenReturn(of());

        when(personMedicalInfo.getMedicationsFor(tenley)).thenReturn(of("ibupurin:200mg"));
        when(personMedicalInfo.getAllergiesFor(tenley)).thenReturn(of("peanut"));

        List<PersonInfoDTO> result = service.getPersonInfoByLastName("Boyd");

        assertNotNull(result);
        assertEquals(3, result.size(), "Three Boyds expected");

        Map<String, PersonInfoDTO> byFirstName = result.stream()
                .collect(Collectors.toMap(PersonInfoDTO::getFirstName, Function.identity()));

        PersonInfoDTO johnDTO = byFirstName.get("John");
        assertNotNull(johnDTO, "John must be present");
        assertEquals("Boyd", johnDTO.getLastName());
        assertEquals(40, johnDTO.getAge());
        assertEquals("1509 Culver St", johnDTO.getAddress());
        assertEquals("john.boyd@email.com", johnDTO.getEmail());
        assertTrue(johnDTO.getMedications().contains("aznol:200mg"));
        assertTrue(johnDTO.getAllergies().contains("nillacilan"));

        PersonInfoDTO jacobDTO = byFirstName.get("Jacob");
        assertNotNull(jacobDTO, "Jacob must be present");
        assertEquals(36, jacobDTO.getAge());
        assertEquals("jacob.boyd@email.com", jacobDTO.getEmail());
        assertTrue(jacobDTO.getMedications().contains("pharmacol:5000mg"));
        assertTrue(jacobDTO.getAllergies().isEmpty());

        PersonInfoDTO tenleyDTO = byFirstName.get("Tenley");
        assertNotNull(tenleyDTO, "Tenley must be present");
        assertEquals(12, tenleyDTO.getAge());
        assertEquals("tenley.boyd@email.com", tenleyDTO.getEmail());
        assertTrue(tenleyDTO.getMedications().contains("ibupurin:200mg"));
        assertTrue(tenleyDTO.getAllergies().contains("peanut"));
    }

    @Test
    public void getPersonInfo_UnknownLastName_ThrowsPersonNotFound() {
        assertThrows(PersonNotFoundException.class,
                () -> service.getPersonInfoByLastName("Unknown"));
    }


}