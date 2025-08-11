package com.safetynet.alerts.service;

import com.safetynet.alerts.model.FireStation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FireStationServiceImplTest {

    @Autowired
    private FireStationService fireStationService;

    @Test
    public void whenFindingExistingFireStation_thenReturnsCorrectFireStation() {
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
    public void whenFindingUnknownFireStation_thenReturnsNull() {
        // Arrange
        String address = "Unknown";

        // Act
        FireStation station = fireStationService.getFireStation(address);

        // Assert
        assertNull(station, "An unknown address should return null");
    }

}

