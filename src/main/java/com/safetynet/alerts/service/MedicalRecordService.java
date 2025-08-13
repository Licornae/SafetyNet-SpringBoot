package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;

public interface MedicalRecordService {
    MedicalRecord getMedicalRecord(String firstName, String lastName);
    MedicalRecord addMedicalRecord(MedicalRecord medicalRecord);
    MedicalRecord updateMedicalRecord(String firstName, String lastName, MedicalRecord updatedMedicalRecord);
}
