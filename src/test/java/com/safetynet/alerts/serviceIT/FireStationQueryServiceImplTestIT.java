package com.safetynet.alerts.serviceIT;

import com.safetynet.alerts.dto.FirestationCoverageDTO;
import com.safetynet.alerts.exception.StationNotFoundException;
import com.safetynet.alerts.repository.DataRepository;
import com.safetynet.alerts.service.FireStationQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class FireStationQueryServiceImplTestIT {

    @Autowired
    FireStationQueryService service;

    @Autowired
    DataRepository dataRepository;

    @BeforeEach
    public void reset() {
        dataRepository.reloadData();
    }

    @Test
    public void testGetCoverageByStation_shouldWorkWithRealDataset() {

        String station = "1";

        FirestationCoverageDTO dto = service.getCoverageByStation(station);

        assertNotNull(dto.getPersons(), "The firestation should cover persons");
        assertEquals(dto.getPersons().size(), dto.getAdults() + dto.getChildren());
        assertEquals(1, dto.getStation());
    }

    @Test
    public void testGetCoverageByStation_unknownStation_throws() {
        assertThrows(StationNotFoundException.class,
                () -> service.getCoverageByStation("20"));
    }

    @Test
    public void testGetCoverageByStation_blankStation_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getCoverageByStation(" "));
    }

    @Test
    public void testGetCoverageByStation_nullStation_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getCoverageByStation(null));
    }
}
