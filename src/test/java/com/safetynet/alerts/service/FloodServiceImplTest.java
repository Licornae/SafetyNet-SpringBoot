package com.safetynet.alerts.service;


import com.safetynet.alerts.dto.FloodHouseholdDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.safetynet.alerts.repository.DataRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FloodServiceImplTest {

    @Autowired
    DataRepository dataRepository;

    @Autowired
    FloodService floodService;

    @BeforeEach
    void reload() { dataRepository.reloadData(); }

    @Test
    void returnsHouseholdsForStations() {
        List<FloodHouseholdDTO> households = floodService.getHouseholdsByStations(List.of("1","3"));
        assertNotNull(households);
        assertFalse(households.isEmpty());
        assertNotNull(households.get(0).getResidents());
    }

    @Test
    void unknownStations_throw404() {
        assertThrows(AddressNotFoundException.class,
                () -> floodService.getHouseholdsByStations(List.of("999")));
    }

    @Test
    void blankList_throw400() {
        assertThrows(IllegalArgumentException.class,
                () -> floodService.getHouseholdsByStations(List.of()));
    }
}

