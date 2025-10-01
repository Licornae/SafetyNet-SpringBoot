package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.exception.StationNotFoundException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.FireStation;
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
public class PhoneAlertServiceImplUnitTest {

    @Mock
    DataRepository dataRepository;

    @Mock
    PeopleWithAgeService peopleWithAgeService;

    @InjectMocks
    PhoneAlertServiceImpl service;

    private DataContainer container;

    @BeforeEach
    public void setUp() {

        List<FireStation> firestations = new ArrayList<>(of(
                new FireStation("1509 Culver St", "3"),
                new FireStation("834 Binoc Ave", "3"),
                new FireStation("644 Gershwin Cir", "1")
        ));

        container = new DataContainer();
        container.setFirestations(firestations);
    }

    @Test
    public void getPhonesByFirestation_existing_returnsUniqueList() {

        when(dataRepository.getDataContainer()).thenReturn(container);

        List<PersonDTO> people = of(
                new PersonDTO("John", "Boyd", 40, "1509 Culver St", "841-874-6512"),
                new PersonDTO("Jacob", "Boyd", 36, "1509 Culver St", "841-874-6513"),
                new PersonDTO("Tenley", "Boyd", 13, "1509 Culver St", " 841-874-6512 "),
                new PersonDTO("Tessa", "Carman", 13, "834 Binoc Ave", "841-874-7777"),
                new PersonDTO("Peter", "Duncan", 24, "644 Gershwin Cir", "841-874-7458")
        );
        when(peopleWithAgeService.listAll()).thenReturn(people);

        List<String> phones = service.getPhonesByFirestation("3");

        assertNotNull(phones);
        assertFalse(phones.isEmpty(), "the list should not be empty");
        assertEquals(3, phones.size(), "There are 3 unique phones for station 3");
    }

    @Test
    public void getPhonesByFirestation_UnknownStation_ThrowsStationNotFound() {

        when(dataRepository.getDataContainer()).thenReturn(container);

        StationNotFoundException ex = assertThrows(
                StationNotFoundException.class,
                () -> service.getPhonesByFirestation("999")
        );
        assertEquals("This station doesn't exist", ex.getMessage());
    }

}
