package com.safetynet.alerts.serviceIT;

import com.safetynet.alerts.exception.StationNotFoundException;
import com.safetynet.alerts.repository.DataRepository;
import com.safetynet.alerts.service.PhoneAlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class PhoneAlertServiceImplTestIT {

    @Autowired
    DataRepository dataRepository;

    @Autowired
    PhoneAlertService phoneAlertService;

    @BeforeEach
    public void resetData() {
        dataRepository.reloadData();
    }

    @Test
    public void testGetPhonesByFirestation_returnsPhonesList() {
        String station = "3";

        List<String> phones = phoneAlertService.getPhonesByFirestation(station);
        assertNotNull(phones, "The list doesn't have to be null");
        assertFalse(phones.isEmpty(), "The list must not be empty for an existing station");
        assertTrue(isUnique(phones), "Each number is unique");
    }

    @Test
    public void testGetPhonesByFirestation_unknownStation_throwsStationNotFound() {
        StationNotFoundException exception = assertThrows(
                StationNotFoundException.class,
                () -> phoneAlertService.getPhonesByFirestation("999")
        );
        assertEquals("This station doesn't exist", exception.getMessage());
    }

    @Test
    public void testGetPhonesByFirestation_blank_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> phoneAlertService.getPhonesByFirestation(" "),
                "An empty station parameter should raise  IllegalArgumentException");
        assertThrows(IllegalArgumentException.class,
                () -> phoneAlertService.getPhonesByFirestation(null),
                "A null station parameter should raise  IllegalArgumentException");
    }

    private static boolean isUnique(List<String> list) {
        Set<String> set = new HashSet<>(list);
        return set.size() == list.size();
    }

}
