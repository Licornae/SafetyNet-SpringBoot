package com.safetynet.alerts.exception;

public class MedicalRecordNotFoundException extends RuntimeException {
    public MedicalRecordNotFoundException(){super("Medical record not found");}
    public MedicalRecordNotFoundException(String message) {
        super(message);
    }
}
