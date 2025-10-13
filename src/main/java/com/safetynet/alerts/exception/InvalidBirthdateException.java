package com.safetynet.alerts.exception;

/**
 * Raised when the provided birthdate value is invalid (format or other issues).
 * Typically mapped to HTTP 400 Bad Request.
 */
public class InvalidBirthdateException extends RuntimeException {
    public InvalidBirthdateException(String value, String reason) {
        super("Invalid birthdate (" + reason + "): " + value);
    }
}
