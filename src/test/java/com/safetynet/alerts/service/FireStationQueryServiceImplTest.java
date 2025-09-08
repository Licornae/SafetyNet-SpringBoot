package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FirestationCoverageDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class FireStationQueryServiceImplTest {

    @Autowired
    FireStationQueryService service;

    @Autowired
    DataRepository dataRepository;

    @BeforeEach
    void reset() {
        dataRepository.reloadData();
    }

    @Test
    void getCoverageByStation_shouldWorkWithRealDataset() {

        FirestationCoverageDTO dto = service.getCoverageByStation("1");

        assertNotNull(dto.getPersons(), "The firestation should cover persons");
        assertEquals(dto.getPersons().size(), dto.getAdults() + dto.getChildren());
        assertEquals(1, dto.getStation());
    }

    @Test
    void getCoverageByStation_unknownStation_throws() {
        assertThrows(AddressNotFoundException.class,
                () -> service.getCoverageByStation("20"));
    }

    @Test
    void getCoverageByStation_blankStation_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getCoverageByStation(" "));
    }

    @Test
    void getCoverageByStation_nullStation_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getCoverageByStation(null));
    }
}
