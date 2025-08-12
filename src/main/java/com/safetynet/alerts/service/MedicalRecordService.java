package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;

public interface MedicalRecordService {
    MedicalRecord getMedicalRecord(String firstName, String lastName);
}
