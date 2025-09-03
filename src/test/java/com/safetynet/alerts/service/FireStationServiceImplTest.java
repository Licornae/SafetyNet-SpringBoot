package com.safetynet.alerts.service;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FireStationServiceImplTest {

    @Autowired
    private FireStationService fireStationService;

    @Autowired
    DataRepository dataRepository;

    @BeforeEach
    void resetData() {
        dataRepository.reloadData();
    }

    @Test
    public void testWhenFindingExistingFireStation_thenReturnsCorrectFireStation() {
        // Arrange : cette adresse doit exister dans data.json
        String address = "1509 Culver St";

        // Act
        FireStation station = fireStationService.getFireStation(address);

        // Assert
        assertNotNull(station, "The searched fire station should exist");
        assertEquals("1509 Culver St", station.getAddress());
        assertEquals("3", station.getStation());
    }

    @Test
    public void testWhenFindingUnknownFireStation_thenReturnsNull() {
        // Arrange
        String address = "Unknown";

        // Act
        FireStation station = fireStationService.getFireStation(address);

        // Assert
        assertNull(station, "An unknown address should return null");
    }

    @Test
    public void testWhenDeletingExistingFireStationAddress_thenAddressIsRemoved() {
        // Arrange
        String address = "1509 Culver St";
        fireStationService.addFireStation(new FireStation(address, "3"));
        // Act
        boolean deleted = fireStationService.deleteFireStationByAddress(address);
        // Assert
        assertTrue(deleted, "Should return true for an existing address");
        assertNull(fireStationService.getFireStation(address), "The address should not exist after deletion");
    }

    @Test
    public void whenDeletingUnknownFireStationAddress_thenReturnFalse() {
        // Arrange
        String unknownAddress = "Unknown Address";
        // Act
        boolean deleted = fireStationService.deleteFireStationByAddress(unknownAddress);
        // Assert
        assertFalse(deleted, "Delete an unknown address should return false");
    }

}

