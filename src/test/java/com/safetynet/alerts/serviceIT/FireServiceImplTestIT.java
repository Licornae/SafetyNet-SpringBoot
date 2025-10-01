package com.safetynet.alerts.serviceIT;

import com.safetynet.alerts.dto.FireDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FireServiceImplTestIT {

    @Autowired
    DataRepository dataRepository;

    @Autowired
    FireService fireService;

    @BeforeEach
    void reload() {
        dataRepository.reloadData();
    }

    @Test
    public void testGetResidentsInfosAtTheAddress_returnsResidentsInfosAndStation(){

        FireDTO fireDTO = fireService.getResidentsByAddress("1509 Culver St");

        assertNotNull(fireDTO);
        assertEquals("3", fireDTO.getStation());
        assertNotNull(fireDTO.getResidents());
    }

    @Test
    public void testGetUnknownAddress_throws404() {
        assertThrows(AddressNotFoundException.class,
                () -> fireService.getResidentsByAddress("Unknown Address"));
    }

    @Test
    public void testGetBlank_throws400() {
        assertThrows(IllegalArgumentException.class,
                () -> fireService.getResidentsByAddress(" "));
    }
}
