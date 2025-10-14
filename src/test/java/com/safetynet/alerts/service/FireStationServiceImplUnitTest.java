package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.exception.DuplicateFireStationException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import static java.util.List.of;

@ExtendWith(MockitoExtension.class)
public class FireStationServiceImplUnitTest {

    @Mock
    DataRepository dataRepository;

    @InjectMocks
    FireStationServiceImpl service;

    private DataContainer container;

    private static final String CULVER = "1509 Culver St";
    private static final String GERSHWIN = "644 Gershwin Cir";

    @BeforeEach
    public void setUp() {

        List<FireStation> firestations = new ArrayList<>(of(
                new FireStation(CULVER, "3"),
                new FireStation(GERSHWIN, "1")
        ));

        container = new DataContainer();
        container.setFirestations(firestations);

        when(dataRepository.getDataContainer()).thenReturn(container);
    }

    //GET
    @Test
    public void getFireStationByAddress_ReturnsFireStation() {
        FireStation fireStation = service.getFireStation(CULVER);
        assertNotNull(fireStation);
        assertEquals(CULVER, fireStation.getAddress());
        assertEquals("3", fireStation.getStation());
    }

    @Test
    public void getFireStation_Unknown_ReturnsNull() {
        assertNull(service.getFireStation("Unknown"));
    }

    //ADD
    @Test
    public void addFireStation_CreateNewStationWithAddress() {
        FireStation created = service.addFireStation(new FireStation("29 15th St", "2"));

        assertNotNull(created);
        assertEquals("29 15th St", created.getAddress());
        assertEquals("2", created.getStation());

        assertTrue(container.getFirestations().stream()
                .anyMatch(fireStation -> "29 15th St".equals(fireStation.getAddress()) && "2".equals(fireStation.getStation())));
    }

    @Test
    public void addFireStation_DuplicateAddress_ThrowsDuplicateFireStationException() {
        int sizeBefore = container.getFirestations().size();

        DuplicateFireStationException ex = assertThrows(DuplicateFireStationException.class,
                () -> service.addFireStation(new FireStation(CULVER, "9")));
        assertTrue(ex.getMessage().contains(CULVER));

        assertEquals(sizeBefore, container.getFirestations().size());

        long culverCount = container.getFirestations().stream().filter(fs -> CULVER.equals(fs.getAddress())).count();
        assertEquals(1, culverCount);
    }

    //UPDATE
    @Test
    public void updateStationAddress_Existing_Updates() {
        FireStation updated = service.updateStationAddress(CULVER, new FireStation(CULVER, "2"));
        assertNotNull(updated);
        assertEquals(CULVER, updated.getAddress());
        assertEquals("2", updated.getStation());
    }

    @Test
    public void updateStationAddress_Unknown_ThrowsAddressNotFound() {
        assertThrows(AddressNotFoundException.class,
                () -> service.updateStationAddress("Unknown", new FireStation("Unknown", "9")));
    }

    //DELETE BY ADDRESS
    @Test
    public void deleteFireStationByAddress_Existing_Removes() {
        assertTrue(service.deleteFireStationByAddress(CULVER));
        assertNull(service.getFireStation(CULVER), "Address should be remove");
    }

    @Test
    public void deleteFireStationByAddress_Unknown_ThrowsAddressNotFound() {
        int sizeBefore = container.getFirestations().size();

        assertThrows(AddressNotFoundException.class,
                () -> service.deleteFireStationByAddress("Unknown"));

        assertEquals(sizeBefore, container.getFirestations().size());
        assertNotNull(service.getFireStation(CULVER));
        assertNotNull(service.getFireStation(GERSHWIN));
    }


    //DELETE BY STATION
    @Test
    public void deleteFireStationsByStation_Existing_ReturnsTrue_RemovesAllMatches() {
        assertTrue(service.deleteFireStationsByStation("3"));
        assertEquals(0, service.countByStation("3"));
        assertNull(service.getFireStation(CULVER));
    }


}
