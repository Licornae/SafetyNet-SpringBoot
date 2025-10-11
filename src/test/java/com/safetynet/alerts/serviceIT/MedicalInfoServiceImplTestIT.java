package com.safetynet.alerts.serviceIT;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import com.safetynet.alerts.service.MedicalInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MedicalInfoServiceImplTestIT {

    @Autowired
    DataRepository dataRepository;

    @Autowired
    MedicalInfoService medicalInfoService;

    @BeforeEach
    public void reload() {
        dataRepository.reloadData();
    }

    @Test
    public void testFindMedicalRecord_ExistingPerson_ReturnsRecord() {
        MedicalRecord medicalRecord = medicalInfoService.findMedicalRecordByName("John", "Boyd");
        assertNotNull(medicalRecord, "John Boyd should exist in the dataset");
        assertEquals("John", medicalRecord.getFirstName());
        assertEquals("Boyd", medicalRecord.getLastName());
        assertNotNull(medicalRecord.getBirthdate(), "The birthdate should exist");
    }

    @Test
    public void testFindMedicalRecord_UnknownPerson_ReturnsNull() {
        assertNull(medicalInfoService.findMedicalRecordByName("Unknown", "Person"));
    }

    @Test
    public void testFindMedicalRecord_NullInputs_ReturnsNull() {
        assertNull(medicalInfoService.findMedicalRecordByName(null, "Boyd"));
        assertNull(medicalInfoService.findMedicalRecordByName("John", null));
        assertNull(medicalInfoService.findMedicalRecordByName(null, null));
    }

    @Test
    public void testGetAgeFor_ExistingPerson_ReturnsPositiveAge() {
        Person john = new Person(); john.setFirstName("John"); john.setLastName("Boyd");
        Integer age = medicalInfoService.getAgeFor(john);
        assertNotNull(age, "The age should not be null");
    }

    @Test
    public void testGetMedicationsFor_ExistingPerson_ReturnsList() {
        Person john = new Person(); john.setFirstName("John"); john.setLastName("Boyd");
        List<String> meds = medicalInfoService.getMedicationsFor(john);

        assertNotNull(meds);
        assertFalse(meds.isEmpty(), "John Boyd have meds in the dataset");
    }

    @Test
    public void testGetAllergiesFor_ExistingPerson_ReturnsList() {
        Person john = new Person(); john.setFirstName("John"); john.setLastName("Boyd");
        List<String> allergies = medicalInfoService.getAllergiesFor(john);

        assertNotNull(allergies, "John Boyd have allergies in the dataset");
    }
}
