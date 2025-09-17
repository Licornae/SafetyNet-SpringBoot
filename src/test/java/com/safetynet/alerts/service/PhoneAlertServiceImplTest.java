package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.StationNotFoundException;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class PhoneAlertServiceImplTest {

    @Autowired
    DataRepository dataRepository;

    @Autowired
    PhoneAlertService phoneAlertService;

    @BeforeEach
    void resetData() {
        dataRepository.reloadData();
    }

    @Test
    void testGetPhonesByFirestation_returnsPhonesList() {
        String station = "3";

        List<String> phones = phoneAlertService.getPhonesByFirestation(station);

        assertNotNull(phones, "La liste ne doit pas être nulle");
        assertFalse(phones.isEmpty(), "La liste ne doit pas être vide pour une station existante");
        assertTrue(isUnique(phones), "Chaque numéro est unique");
    }

    @Test
    void testGetPhonesByFirestation_unknownStation_throwsStationNotFound() {
        StationNotFoundException exception = assertThrows(
                StationNotFoundException.class,
                () -> phoneAlertService.getPhonesByFirestation("999")
        );
        assertEquals("Cette station n'existe pas", exception.getMessage());
    }

    @Test
    void testGetPhonesByFirestation_blank_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> phoneAlertService.getPhonesByFirestation(" "),
                "Un paramètre station vide doit lever IllegalArgumentException");
        assertThrows(IllegalArgumentException.class,
                () -> phoneAlertService.getPhonesByFirestation(null),
                "Un paramètre station null doit lever IllegalArgumentException");
    }

    private static boolean isUnique(List<String> list) {
        Set<String> set = new HashSet<>(list);
        return set.size() == list.size();
    }

}
