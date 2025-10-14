package com.safetynet.alerts.service;


import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.safetynet.alerts.util.AgeCalculator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalInfoServiceImplUnitTest {

    @Mock DataRepository dataRepository;
    @InjectMocks MedicalInfoServiceImpl service;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private static Person person(String firstName, String lastName) {
        Person person = new Person(); person.setFirstName(firstName); person.setLastName(lastName); return person;
    }
    private static MedicalRecord medicalRecord(String firstName, String lastName) {
        MedicalRecord medicalRecord = mock(MedicalRecord.class);
        when(medicalRecord.getFirstName()).thenReturn(firstName);
        when(medicalRecord.getLastName()).thenReturn(lastName);
        return medicalRecord;
    }
    private static DataContainer dataContainer(List<MedicalRecord> medicalRecords) {
        DataContainer dataContainer = new DataContainer(); dataContainer.setMedicalrecords(medicalRecords); return dataContainer;
    }

    //FIND MEDICAL RECORD
    @Test
    public void findMedicalRecordByName_Match_ReturnsRecord() {
        MedicalRecord john = medicalRecord("John", "Doe");
        MedicalRecord jane = medicalRecord("Jane", "Doe");
        when(dataRepository.getDataContainer()).thenReturn(dataContainer(of(john, jane)));

        MedicalRecord found = service.findMedicalRecordByName("Jane", "Doe");

        assertNotNull(found);
        assertEquals("Jane", found.getFirstName());
        assertEquals("Doe", found.getLastName());
    }

    @Test
    public void findMedicalRecordByName_NoMatch_ReturnsNull() {
        MedicalRecord other = medicalRecord("Alice", "Smith");

        MedicalRecord incomplete = mock(MedicalRecord.class);
        when(incomplete.getFirstName()).thenReturn(null);

        java.util.List<MedicalRecord> records = new java.util.ArrayList<>();
        records.add(null);
        records.add(incomplete);
        records.add(other);

        when(dataRepository.getDataContainer()).thenReturn(dataContainer(records));

        MedicalRecord found = service.findMedicalRecordByName("John", "Doe");

        assertNull(found);
        verify(dataRepository).getDataContainer();
    }



    @Test
    public void findMedicalRecordByName_NullInputs_ReturnsNull() {
        assertNull(service.findMedicalRecordByName(null, "Doe"));
        assertNull(service.findMedicalRecordByName("John", null));
        assertNull(service.findMedicalRecordByName(null, null));
        verifyNoInteractions(dataRepository);
    }

    //GET AGE
    @Test
    public void getAgeFor_ExistingPerson_ValidBirthdate_ReturnsAge() {
        String birth = LocalDate.now().minusYears(25).format(FORMAT);
        MedicalRecord john = medicalRecord("John", "Doe");
        when(john.getBirthdate()).thenReturn(birth);
        when(dataRepository.getDataContainer()).thenReturn(dataContainer(of(john)));

        Integer age = service.getAgeFor(person("John", "Doe"));

        assertNotNull(age);
        assertEquals(AgeCalculator.computeAge(birth), age);
    }

    @Test
    public void getAgeFor_InvalidBirthdate_ReturnsNull() {
        MedicalRecord john = medicalRecord("John", "Doe");
        when(john.getBirthdate()).thenReturn("not a date");
        when(dataRepository.getDataContainer()).thenReturn(dataContainer(of(john)));

        assertNull(service.getAgeFor(person("John", "Doe")));
    }

    //GET MEDICATIONS
    @Test
    public void getMedicationsFor_ExistingPerson_ReturnsList() {
        MedicalRecord john = medicalRecord("John", "Boyd");
        when(john.getMedications()).thenReturn(of("aznol:200mg", "hydrapermazol:100mg"));
        when(dataRepository.getDataContainer()).thenReturn(dataContainer(of(john)));

        List<String> meds = service.getMedicationsFor(person("John", "Boyd"));

        assertNotNull(meds);
        assertEquals(2, meds.size());
        assertTrue(meds.contains("aznol:200mg"));
    }

    @Test
    public void getMedicationsFor_MedicationsNull_ReturnsEmptyList() {
        MedicalRecord john = medicalRecord("John", "Boyd");
        when(john.getMedications()).thenReturn(null);
        when(dataRepository.getDataContainer()).thenReturn(dataContainer(of(john)));

        List<String> meds = service.getMedicationsFor(person("John", "Boyd"));

        assertNotNull(meds);
        assertTrue(meds.isEmpty());
    }

    //GET ALLERGIES
    @Test
    public void getAllergiesFor_ExistingPerson_ReturnsList() {
        MedicalRecord john = medicalRecord("John", "Boyd");
        when(john.getAllergies()).thenReturn(of("peanut"));
        when(dataRepository.getDataContainer()).thenReturn(dataContainer(of(john)));

        List<String> allergies = service.getAllergiesFor(person("John", "Boyd"));

        assertNotNull(allergies);
        assertEquals(1, allergies.size());
        assertTrue(allergies.contains("peanut"));
    }

    @Test
    public void getAllergiesFor_AllergiesNull_ReturnsEmptyList() {
        MedicalRecord john = medicalRecord("John", "Boyd");
        when(john.getAllergies()).thenReturn(null);
        when(dataRepository.getDataContainer()).thenReturn(dataContainer(of(john)));

        List<String> allergies = service.getAllergiesFor(person("John", "Boyd"));

        assertNotNull(allergies);
        assertTrue(allergies.isEmpty());
    }
}