package com.safetynet.alerts.serviceIT;

import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.DataRepository;
import com.safetynet.alerts.service.FireStationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FireStationServiceImplTestIT {

    @Autowired
    private FireStationService fireStationService;

    @Autowired
    DataRepository dataRepository;

    @BeforeEach
    public void resetData() {
        dataRepository.reloadData();
    }

    @Test
    public void testWhenFindingExistingFireStation_thenReturnsCorrectFireStation() {
        // Arrange
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
        String address = "1509 Culver St";

        assertNotNull(fireStationService.getFireStation(address), "Address must exist before deletion");

        boolean deleted = fireStationService.deleteFireStationByAddress(address);

        assertTrue(deleted, "Should return true for an existing address");
        assertNull(fireStationService.getFireStation(address), "The address should not exist after deletion");
    }

    @Test
    public void testWhenDeletingUnknownFireStationAddress_ThenThrowsAddressNotFound() {
        String unknownAddress = "Unknown Address";

        assertThrows(AddressNotFoundException.class,
                () -> fireStationService.deleteFireStationByAddress(unknownAddress),
                "Deleting an unknown address must throw AddressNotFoundException");
    }
}

