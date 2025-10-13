package com.safetynet.alerts.exception;

/**
 * Thrown when attempting to create a medical record that already exists (same firstName + lastName).
 * Intended to be translated to HTTP 409 Conflict.
 */
public class DuplicateMedicalRecordException extends RuntimeException {
    public DuplicateMedicalRecordException(String message) {
        super(message);
    }
}
