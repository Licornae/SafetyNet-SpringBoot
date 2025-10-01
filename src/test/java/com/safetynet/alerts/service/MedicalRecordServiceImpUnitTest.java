package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.MedicalRecordNotFoundException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.ArrayList;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceImpUnitTest {

    @Mock
    DataRepository dataRepository;

    @InjectMocks
    MedicalRecordServiceImpl service;

    private DataContainer container;
    private List<MedicalRecord> medicalRecords;

    private MedicalRecord johnBoyd, jacobBoyd, tenleyBoyd;

    @BeforeEach
    public void setUp() {

        johnBoyd = new MedicalRecord("John", "Boyd", "03/06/1984",
                of("aznol:350mg", "hydrapermazol:100mg"),
                of("nillacilan")
        );
        jacobBoyd = new MedicalRecord("Jacob", "Boyd", "03/06/1989",
                of("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg"),
                of()
        );
        tenleyBoyd = new MedicalRecord("Tenley", "Boyd", "02/18/2012",
                of(),
                of("peanut")
        );

        medicalRecords = new ArrayList<>(of(johnBoyd, jacobBoyd, tenleyBoyd));

        container = new DataContainer();
        container.setMedicalrecords(medicalRecords);

        when(dataRepository.getDataContainer()).thenReturn(container);
    }

    // GET
    @Test
    public void getMedicalRecord_Existing_ReturnsRecord() {

        MedicalRecord medicalRecord = service.getMedicalRecord("John", "Boyd");
        assertNotNull(medicalRecord, "The searched medical record should exist");
        assertEquals("John", medicalRecord.getFirstName());
        assertEquals("Boyd", medicalRecord.getLastName());
        assertEquals("03/06/1984", medicalRecord.getBirthdate());
        assertEquals(of("aznol:350mg", "hydrapermazol:100mg"), medicalRecord.getMedications());
        assertEquals(of("nillacilan"), medicalRecord.getAllergies());
    }

    @Test
    public void getMedicalRecord_Unknown_ReturnsNull() {
        MedicalRecord medicalRecord = service.getMedicalRecord("Nonexistent", "Nobody");
        assertNull(medicalRecord, "Unknown person should return null");
    }

    // ADD
    @Test
    public void addMedicalRecord_CreateANewMedicalRecord() {
        MedicalRecord newMedicalRecord = new MedicalRecord(
                "Peter", "Duncan", "09/06/2000", of(), of("shellfish")
        );

        MedicalRecord saved = service.addMedicalRecord(newMedicalRecord);

        assertSame(newMedicalRecord, saved, "Service should return the same instance it added");
        assertTrue(container.getMedicalrecords().contains(newMedicalRecord), "Container should contain the new record");
        assertEquals(4, container.getMedicalrecords().size(), "One more record should be present");
    }

    //UPDATE
    @Test
    public void updateMedicalRecord_Existing_UpdatesAllFields() {

        MedicalRecord payload = new MedicalRecord(
                "John", "Boyd", "03/06/1999",
                of("aznol:350mg", "hydrapermazol:100mg", "alprazolam:0,25mg"),
                of("nillacilan", "Bet v1")
        );

        MedicalRecord updated = service.updateMedicalRecord("John", "Boyd", payload);

        assertSame(johnBoyd, updated, "Service should update and return the stored instance");
        assertEquals("03/06/1999", updated.getBirthdate());
        assertEquals(of("aznol:350mg", "hydrapermazol:100mg", "alprazolam:0,25mg"), updated.getMedications());

        assertEquals(of("nillacilan", "Bet v1"), updated.getAllergies(),
                "Allergies should be set from the payload's allergies, not medications");
    }

    @Test
    public void updateMedicalRecord_Unknown_ThrowsNotFound() {
        MedicalRecord payload = new MedicalRecord("John", "Unknown", "03/06/1999",
                of("x"), of("y")
        );
        assertThrows(MedicalRecordNotFoundException.class, () ->
                service.updateMedicalRecord("John", "Unknown", payload));
    }

    // DELETE
    @Test
    public void deleteMedicalRecord_Existing_Removes() {
        boolean deleted = service.deleteMedicalRecord("John", "Boyd");
        assertTrue(deleted, "Should return true for an existing MedicalRecord");
        assertNull(service.getMedicalRecord("John", "Boyd"), "Record should not exist after deletion");
        assertEquals(2, container.getMedicalrecords().size());
    }

    @Test
    public void deleteMedicalRecord_Unknown_ReturnsFalse() {
        boolean deleted = service.deleteMedicalRecord("Unknown", "Person");
        assertFalse(deleted, "Deleting an unknown MedicalRecord should return false");
        assertEquals(3, container.getMedicalrecords().size(), "No record should be removed");
    }

}
