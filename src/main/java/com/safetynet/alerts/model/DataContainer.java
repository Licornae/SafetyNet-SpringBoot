package com.safetynet.alerts.model;

import lombok.Data;

import java.util.List;

/**
 * Root container deserialized from data.json.
 * It groups all entities handled by the application: persons, fire stations and medical records.
 */
@Data
public class DataContainer {
        private List<Person> persons;
        private List<FireStation> firestations;
        private List<MedicalRecord> medicalrecords;
    }

