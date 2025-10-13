package com.safetynet.alerts.exception;

/**
 * Thrown when a medical record can not be found for the given person.
 * Typically mapped to HTTP 404 Not Found.
 */
public class MedicalRecordNotFoundException extends RuntimeException {
    public MedicalRecordNotFoundException(){super("Medical record not found");}
    public MedicalRecordNotFoundException(String message) {
        super(message);
    }
}
