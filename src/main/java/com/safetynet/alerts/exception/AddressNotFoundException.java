package com.safetynet.alerts.exception;


/**
 * Thrown when a requested street address cannot be resolved in the dataset.
 */
public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(){super("Address not found");}
    public AddressNotFoundException(String message) {
        super(message);
    }
}