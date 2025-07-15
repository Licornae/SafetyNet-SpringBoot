package com.safetynet.alerts.model;

import lombok.Data;

import java.util.List;

@Data
public class DataContainer {
        private List<Person> persons;
        private List<FireStation> firestations;
        private List<MedicalRecord> medicalrecords;
    }

