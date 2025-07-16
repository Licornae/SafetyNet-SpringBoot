package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DataRepositoryTest {

    @Autowired
    private DataRepository dataRepository; // Injecté par Spring Boot

    @Test
    public void testLoadData(){
        DataContainer dataContainer = dataRepository.getDataContainer();
        assertNotNull(dataContainer, "DataContainer ne doit pas être null");
    }

    @Test
    public void testLoadPersons() {

        List<Person> persons = dataRepository.getDataContainer().getPersons();
        assertNotNull(persons, "La liste des personnes ne doit pas être nulle");
        assertFalse(persons.isEmpty(), "La liste des personnes ne doit pas être vide");

        Person firstPerson = persons.get(0);
        System.out.println("Première personne chargée : " + firstPerson.getFirstName() + " " + firstPerson.getLastName());
    }

    @Test
    public void testLoadFireStations(){
        List<FireStation> fireStations = dataRepository.getDataContainer().getFirestations();
        assertNotNull(fireStations, "La liste des casernes ne doit pas être nulle");
        assertFalse(fireStations.isEmpty(), "La liste des caserne ne doit pas être vide");

        FireStation firstStation = fireStations.get(0);
        System.out.println("Premiere station chargée : " + firstStation.getStation() + " à l'adresse : " + firstStation.getAddress());
    }

    @Test
    public void testLoadMedicalRecords(){
        List<MedicalRecord> medicalRecords = dataRepository.getDataContainer().getMedicalrecords();
        assertNotNull(medicalRecords, "La liste des dossiers médicaux ne doit pas être nulle");
        assertFalse(medicalRecords.isEmpty(), "La liste des dossiers médicaux ne doit pas être nulle");

        MedicalRecord firstMedicalRecord = medicalRecords.get(0);
        System.out.println("Premier dossier médical chargée : " + firstMedicalRecord.getFirstName() + ", "
                + firstMedicalRecord.getBirthdate() + ", " + firstMedicalRecord.getAllergies());
    }
}
