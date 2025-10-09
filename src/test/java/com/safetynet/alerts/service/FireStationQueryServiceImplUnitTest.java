package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FirestationCoverageDTO;
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
public class FireStationQueryServiceImplUnitTest {

    @Mock
    DataRepository dataRepository;

    @Mock
    PeopleWithAgeService peopleWithAgeService;

    @InjectMocks
    FireStationQueryServiceImpl service;

    private DataContainer container;

    private static final String CULVER_ST = "1509 Culver St";
    private static final String GERSHWIN  = "644 Gershwin Cir";

    @BeforeEach
    public void setUp() {

        List<FireStation> stations = new ArrayList<>(of(
                new FireStation(CULVER_ST, "3"),
                new FireStation(GERSHWIN,  "1")
        ));

        container = new DataContainer();
        container.setFirestations(stations);
    }

    @Test
    public void getCoverageByStation_returnsPersonsAndCounts() {
        when(dataRepository.getDataContainer()).thenReturn(container);

        PersonDTO john   = new PersonDTO("John",   "Boyd", 41, CULVER_ST,   "841-874-6512");
        PersonDTO jacob  = new PersonDTO("Jacob",  "Boyd", 36, CULVER_ST,   "841-874-6513");
        PersonDTO tenley = new PersonDTO("Tenley", "Boyd", 13, CULVER_ST,   "841-874-6512");

        PersonDTO peter  = new PersonDTO("Peter",  "Duncan", 20, GERSHWIN, "841-874-6512");

        when(peopleWithAgeService.listAll()).thenReturn(of(john, jacob, tenley, peter));

        FirestationCoverageDTO dto = service.getCoverageByStation("3");

        assertNotNull(dto);
        assertEquals(3, dto.getStation());
        assertNotNull(dto.getPersons());

        assertEquals(3, dto.getPersons().size());
        assertEquals(2, dto.getAdults(), "John, Jacob");
        assertEquals(1, dto.getChildren(), "Tenley");

        assertTrue(dto.getPersons().stream().allMatch(personDTO -> CULVER_ST.equals(personDTO.getAddress())));
        assertEquals("John", dto.getPersons().get(0).getFirstName());
    }

    @Test
    public void getCoverageByStation_UnknownStation_ThrowsStationNotFoundException() {
        when(dataRepository.getDataContainer()).thenReturn(container);
        assertThrows(StationNotFoundException.class, () -> service.getCoverageByStation("999"));
    }

    @Test
    public void getCoverageByStation_StationExistsButOnlyNullOrBlankAddresses_returnsEmptyResult() {
        container.setFirestations(new ArrayList<>(List.of(
                new FireStation(null, "7"),
                new FireStation("   ", "7")
        )));

        when(dataRepository.getDataContainer()).thenReturn(container);

        assertDoesNotThrow(() -> {
            FirestationCoverageDTO dto = service.getCoverageByStation("7");
            assertNotNull(dto);
            assertEquals(7, dto.getStation());
            assertNotNull(dto.getPersons());
            assertTrue(dto.getPersons().isEmpty());
        });
    }

    @Test
    public void getCoverageByStation_blankParam_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> service.getCoverageByStation(" "));
    }

}
