package com.safetynet.alerts.exception;

/**
 * Raised when no data exists for the requested city
 * Typically mapped to HTTP 404 Not Found.
 */
public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String message) {
        super(message);
    }
}
