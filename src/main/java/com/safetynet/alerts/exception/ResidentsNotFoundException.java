package com.safetynet.alerts.exception;

/**
 * Thrown when an address is covered by a station but has no residents recorded.
 */
public class ResidentsNotFoundException extends RuntimeException {
    public ResidentsNotFoundException(String message) {
        super(message);
    }
}
