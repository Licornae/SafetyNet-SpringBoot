package com.safetynet.alerts.exception;

public class InvalidBirthdateException extends RuntimeException {

    private final String value;
    private final String reason;

    public InvalidBirthdateException(String value, String reason) {
        super(message(value, reason));
        this.value = value;
        this.reason = reason;
    }

    public static String message(String value, String reason) {
        return "Invalid birthdate (" + reason + "): " + value;
    }
}
