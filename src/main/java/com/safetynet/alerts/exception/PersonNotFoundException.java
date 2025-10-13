package com.safetynet.alerts.exception;

/**
 * Thrown when the requested person cannot be found in the loaded data.
 * Typically mapped to HTTP 404 Not Found.
 */
public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException() {
        super("Person not found");
    }

    public PersonNotFoundException(String message) {
        super(message);
    }
}
