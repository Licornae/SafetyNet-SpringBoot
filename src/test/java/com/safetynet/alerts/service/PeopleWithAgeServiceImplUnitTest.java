package com.safetynet.alerts.service;
import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PeopleWithAgeServiceImplUnitTest {

    @Mock
    private DataRepository dataRepository;

    @InjectMocks
    private PeopleWithAgeServiceImpl service;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private static String fmt(LocalDate date) {
        return date.format(FORMAT);
    }

    @Test
    public void listAll_ComputesAgeAndMapsFields_WhenMedicalRecordExists() {

        //Persons
        Person p1 = new Person();
        p1.setFirstName("John");
        p1.setLastName("Doe");
        p1.setAddress("1509 Culver St");
        p1.setPhone("111-111-1111");

        Person p2 = new Person();
        p2.setFirstName("Jane");
        p2.setLastName("Doe");
        p2.setAddress("29 15th St");
        p2.setPhone("222-222-2222");

        //Corresponding medical records
        LocalDate bdJohn = LocalDate.now().minusYears(30);
        LocalDate bdJane = LocalDate.now().minusYears(18);

        MedicalRecord mr1 = new MedicalRecord();
        mr1.setFirstName("John");
        mr1.setLastName("Doe");
        mr1.setBirthdate(fmt(bdJohn));

        MedicalRecord mr2 = new MedicalRecord();
        mr2.setFirstName("Jane");
        mr2.setLastName("Doe");
        mr2.setBirthdate(fmt(bdJane));

        DataContainer dataContainer = new DataContainer();
        dataContainer.setPersons(Arrays.asList(p1, p2));
        dataContainer.setMedicalrecords(Arrays.asList(mr1, mr2));

        when(dataRepository.getDataContainer()).thenReturn(dataContainer);

        // Act
        List<PersonDTO> result = service.listAll();

        // Assert
        assertEquals(2, result.size());

        PersonDTO r1 = result.get(0);
        PersonDTO r2 = result.get(1);

        int expectedJohnAge = Period.between(bdJohn, LocalDate.now()).getYears();
        int expectedJaneAge = Period.between(bdJane, LocalDate.now()).getYears();

        // John
        assertEquals("John", r1.getFirstName());
        assertEquals("Doe", r1.getLastName());
        assertEquals("1509 Culver St", r1.getAddress());
        assertEquals("111-111-1111", r1.getPhone());
        assertEquals(expectedJohnAge, r1.getAge());

        // Jane
        assertEquals("Jane", r2.getFirstName());
        assertEquals("Doe", r2.getLastName());
        assertEquals("29 15th St", r2.getAddress());
        assertEquals("222-222-2222", r2.getPhone());
        assertEquals(expectedJaneAge, r2.getAge());
    }


    @Test
    void listAll_ReturnsEmpty_WhenDataContainerNull() {
        when(dataRepository.getDataContainer()).thenReturn(null);

        List<PersonDTO> result = service.listAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
