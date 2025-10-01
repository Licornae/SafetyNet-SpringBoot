package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FireDTO;
import com.safetynet.alerts.dto.PersonMedicalInfoDTO;
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

import java.util.ArrayList;
import java.util.List;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FireServiceImplUnitTest {

    @Mock
    DataRepository dataRepository;

    @Mock
    MedicalInfoService medicalInfoService;

    @InjectMocks
    FireServiceImpl service;

    private Person john;
    private Person jacob;
    private Person tenley;
    private Person roger;
    private Person felicia;

    private DataContainer container;

    @BeforeEach
    public void setup() {

        john   = new Person("John",   "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jaboyd@email.com");
        jacob  = new Person("Jacob",  "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6513", "drk@email.com");
        tenley = new Person("Tenley", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "tenz@email.com");
        roger  = new Person("Roger",  "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jaboyd@email.com");
        felicia= new Person("Felicia","Boyd", "1509 Culver St", "Culver", "97451", "841-874-6544", "jaboyd@email.com");

        Person peter = new Person("Peter","Duncan","644 Gershwin Cir","Culver","97451","841-874-6512","jaboyd@email.com");

        List<FireStation> stations = new ArrayList<>(of(
                new FireStation("1509 Culver St", "3"),
                new FireStation("644 Gershwin Cir", "1")
        ));

        container = new DataContainer();
        container.setPersons(new ArrayList<>(of(john, jacob, tenley, roger, felicia, peter)));
        container.setFirestations(stations);
        container.setMedicalrecords(null);
        when(dataRepository.getDataContainer()).thenReturn(container);
    }

    @Test
    public void getResidentsByAddress_ok_returnsStationAndResidentsWithMedicalInfo() {

        when(medicalInfoService.getAgeFor(john)).thenReturn(41);
        when(medicalInfoService.getAgeFor(jacob)).thenReturn(36);
        when(medicalInfoService.getAgeFor(tenley)).thenReturn(13);
        when(medicalInfoService.getAgeFor(roger)).thenReturn(8);
        when(medicalInfoService.getAgeFor(felicia)).thenReturn(39);

        when(medicalInfoService.getMedicationsFor(john)).thenReturn(of("aznol:350mg", "hydrapermazol:100mg"));
        when(medicalInfoService.getMedicationsFor(jacob)).thenReturn(of("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg"));
        when(medicalInfoService.getMedicationsFor(tenley)).thenReturn(List.of());
        when(medicalInfoService.getMedicationsFor(roger)).thenReturn(List.of());
        when(medicalInfoService.getMedicationsFor(felicia)).thenReturn(of("tetracyclaz:650mg"));

        when(medicalInfoService.getAllergiesFor(john)).thenReturn(of("nillacilan"));
        when(medicalInfoService.getAllergiesFor(jacob)).thenReturn(List.of());
        when(medicalInfoService.getAllergiesFor(tenley)).thenReturn(of("peanut"));
        when(medicalInfoService.getAllergiesFor(roger)).thenReturn(List.of());
        when(medicalInfoService.getAllergiesFor(felicia)).thenReturn(of("xilliathal"));

        FireDTO dto = service.getResidentsByAddress("1509 Culver St");

        assertNotNull(dto);
        assertEquals("3", dto.getStation(), "The station should be 3");

        List<PersonMedicalInfoDTO> residents = dto.getResidents();
        assertNotNull(residents);
        assertEquals(5, residents.size(), "John, Jacob, Tenley, Roger, Felicia");

        PersonMedicalInfoDTO johnDto = residents.stream()
                .filter(personMedicalInfoDTO -> personMedicalInfoDTO.getFirstName().equals("John"))
                .findFirst().orElseThrow();
        assertEquals("Boyd", johnDto.getLastName());
        assertEquals("841-874-6512", johnDto.getPhone());
        assertEquals(41, johnDto.getAge());
        assertTrue(johnDto.getMedications().containsAll(of("aznol:350mg", "hydrapermazol:100mg")));
        assertEquals(of("nillacilan"), johnDto.getAllergies());

        PersonMedicalInfoDTO tenleyDto = residents.stream()
                .filter(personMedicalInfoDTO -> personMedicalInfoDTO.getFirstName().equals("Tenley"))
                .findFirst().orElseThrow();
        assertEquals(13, tenleyDto.getAge());
        assertEquals(List.of(), tenleyDto.getMedications());
        assertEquals(of("peanut"), tenleyDto.getAllergies());
    }

    @Test
    public void getResidentsByAddress_noStationForAddress_throwsAddressNotFound() {
        assertThrows(AddressNotFoundException.class, () -> service.getResidentsByAddress("Unknown Address"));
    }


}
