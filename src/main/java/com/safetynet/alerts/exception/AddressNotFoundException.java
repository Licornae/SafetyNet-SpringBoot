package com.safetynet.alerts.exception;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(){super("Address not found");}
    public AddressNotFoundException(String message) {
        super(message);
    }
}