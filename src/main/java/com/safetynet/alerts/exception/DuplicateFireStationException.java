package com.safetynet.alerts.exception;

/**
 * Raised when attempting to create a fire station mapping that already exists
 * (same address already associated to a station).
 * Intended to be translated to HTTP 409 Conflict.
 */
public class DuplicateFireStationException extends RuntimeException {
    public DuplicateFireStationException(String message) {
        super(message);
    }
}
