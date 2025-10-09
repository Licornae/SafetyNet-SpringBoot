package com.safetynet.alerts.exception;


/**
 * Exception raised when the requested fire station number does not exist
 * in the loaded data.
 */
public class StationNotFoundException extends RuntimeException {
    public StationNotFoundException(String message) {
        super(message);
    }
}
