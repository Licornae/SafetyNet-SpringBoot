package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest
public class MedicalRecordServiceImplTest {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    DataRepository dataRepository;

    @BeforeEach
    void resetData() {
        dataRepository.reloadData();
    }

    @Test
    public void whenFindingMedicalRecord_thenReturnsCorrectMedicalRecord(){
        // Arrange
        String firstName = "John";
        String lastName = "Boyd";

        //Act
        MedicalRecord medicalRecord = medicalRecordService.getMedicalRecord(firstName,lastName);

        //Assert
        assertNotNull(medicalRecord,"The searched medical record should exist");
        assertEquals("John",medicalRecord.getFirstName());
        assertEquals("Boyd",medicalRecord.getLastName());
        assertEquals("03/06/1984",medicalRecord.getBirthdate());
        assertEquals(List.of("aznol:350mg", "hydrapermazol:100mg"), medicalRecord.getMedications());
        assertEquals(List.of("nillacilan"), medicalRecord.getAllergies());
    }

    @Test
    public void whenFindingUnknownPerson_thenReturnsNoMedicalRecord() {
        // Arrange
        String firstName = "Nonexistent";
        String lastName = "Nobody";

        //Act
        MedicalRecord medicalRecord = medicalRecordService.getMedicalRecord(firstName,lastName);

        //Assert
        assertNull(medicalRecord, "An unknown person should return null");
    }


}
