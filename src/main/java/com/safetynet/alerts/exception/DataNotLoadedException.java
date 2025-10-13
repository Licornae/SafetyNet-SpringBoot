package com.safetynet.alerts.exception;

/**
 * Indicates that the application dataset could not be loaded
 */
public class DataNotLoadedException extends RuntimeException {
    public DataNotLoadedException(String message) {
        super(message);
    }
}
