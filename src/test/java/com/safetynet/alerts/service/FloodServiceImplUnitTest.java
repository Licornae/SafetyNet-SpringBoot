package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FloodHouseholdDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
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
public class FloodServiceImplUnitTest {

    @Mock
    DataRepository dataRepository;

    @Mock
    MedicalInfoService medicalInfoService;

    @InjectMocks
    FloodServiceImpl floodService;

    private DataContainer container;

    private static final String CULVER   = "1509 Culver St";
    private static final String GERSHWIN = "644 Gershwin Cir";

    private Person john, jacob, tenley, peter;

    @BeforeEach
    public void setUp() {
        john   = new Person("John",   "Boyd",   CULVER,   "Culver", "97451", "841-874-6512", "john@example.com");
        jacob  = new Person("Jacob",  "Boyd",   CULVER,   "Culver", "97451", "841-874-6513", "jacob@example.com");
        tenley = new Person("Tenley", "Boyd",   CULVER,   "Culver", "97451", "841-874-6512", "tenley@example.com");
        peter  = new Person("Peter",  "Duncan", GERSHWIN, "Culver", "97451", "841-874-6512", "peter@example.com");

        List<Person> persons = List.of(john, jacob, tenley, peter);

        List<FireStation> firestations = List.of(
                new FireStation(CULVER, "3"),
                new FireStation(GERSHWIN, "1")
        );

        DataContainer container = new DataContainer();
        container.setPersons(new ArrayList<>(persons));
        container.setFirestations(new ArrayList<>(firestations));
        container.setMedicalrecords(new ArrayList<>());

        when(dataRepository.getDataContainer()).thenReturn(container);
    }

    @Test
    public void getHouseholdsByStations_station1And3_returnsBothAddresses() {

        when(medicalInfoService.getAgeFor(john)).thenReturn(41);
        when(medicalInfoService.getAgeFor(jacob)).thenReturn(36);
        when(medicalInfoService.getAgeFor(tenley)).thenReturn(13);
        when(medicalInfoService.getAgeFor(peter)).thenReturn(20);

        when(medicalInfoService.getMedicationsFor(john)).thenReturn(List.of("aznol:350mg","hydrapermazol:100mg"));
        when(medicalInfoService.getAllergiesFor(john)).thenReturn(List.of("nillacilan"));

        when(medicalInfoService.getMedicationsFor(jacob)).thenReturn(List.of("pharmacol:5000mg","terazine:10mg"));
        when(medicalInfoService.getAllergiesFor(jacob)).thenReturn(List.of());

        when(medicalInfoService.getMedicationsFor(tenley)).thenReturn(List.of());
        when(medicalInfoService.getAllergiesFor(tenley)).thenReturn(List.of("peanut"));

        when(medicalInfoService.getMedicationsFor(peter)).thenReturn(List.of());
        when(medicalInfoService.getAllergiesFor(peter)).thenReturn(List.of());

        List<FloodHouseholdDTO> households = floodService.getHouseholdsByStations(of("1", "3"));
        assertNotNull(households);
        assertEquals(2, households.size(), "Should return 2 address");

        FloodHouseholdDTO culver = households.stream()
                .filter(h -> CULVER.equals(h.getAddress()))
                .findFirst().orElse(null);
        assertNotNull(culver);
        assertEquals("3", culver.getStation());
        assertEquals(3, culver.getResidents().size());

        FloodHouseholdDTO gershwin = households.stream()
                .filter(h -> GERSHWIN.equals(h.getAddress()))
                .findFirst().orElse(null);
        assertNotNull(gershwin);
        assertEquals("1", gershwin.getStation());
        assertEquals(1, gershwin.getResidents().size());
        assertEquals("Peter", gershwin.getResidents().get(0).getFirstName());
        assertEquals(20, gershwin.getResidents().get(0).getAge());
    }

    @Test
    public void getHouseholdsByStations_unknownStations_throw404() {
        assertThrows(AddressNotFoundException.class,
                () -> floodService.getHouseholdsByStations(of("999")));
    }
}
