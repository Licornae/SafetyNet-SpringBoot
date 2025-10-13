package com.safetynet.alerts.exception;

/**
 * Raised when a person already exists (same firstName + lastName).
 * Used to return an HTTP 409 Conflict.
 */
public class DuplicatePersonException extends RuntimeException {
    public DuplicatePersonException(String message) {
        super(message);
    }
}
