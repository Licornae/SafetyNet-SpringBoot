package com.safetynet.alerts.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Global exception translator for the API.
 * Produce a compact JSON error body containing message, path, and timestamp.
 * Typical body:
 *  {
 *    "message": "...",
 *    "path": "/...",
 *    "timestamp": "2025-10-08T10:15:30+02:00"
 *   }
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> errorBody(String message, HttpServletRequest req) {
        return Map.of(
                "message", message,
                "path", req.getRequestURI(),
                "timestamp", OffsetDateTime.now().toString()
        );
    }

    /**
     * Not found exceptions to HTTP 404.
     * Behavior:
     * - Returns a JSON with message, path, and timestamp.
     *
     * @param exception the not found exception raised by the domain layer
     * @param req the current HTTP request
     * @return 404 Not Found with an error payload
     */
    @ExceptionHandler({
            PersonNotFoundException.class,
            AddressNotFoundException.class,
            MedicalRecordNotFoundException.class,
            StationNotFoundException.class,
            CityNotFoundException.class,
            ResidentsNotFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException exception, HttpServletRequest req) {

        log.error("404 Not Found [{} {}] {}: {}", req.getMethod(), req.getRequestURI(), exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody(exception.getMessage(), req));
    }

    /**
     * Maps duplicate domain exceptions to HTTP 409 Conflict.
     * Behavior:
     * - Returns JSON with message, path, and timestamp.
     *
     * @param exception the duplicate/conflict exception raised by the domain layer
     * @param req current HTTP request
     * @return ResponseEntity with status 409 and error payload
     */
    @ExceptionHandler({DuplicateMedicalRecordException.class, DuplicatePersonException.class, DuplicateFireStationException.class})
    public ResponseEntity<Map<String, Object>> handleConflict(RuntimeException exception,
                                                              HttpServletRequest req) {
        log.error("409 Conflict [{} {}] {}: {}", req.getMethod(), req.getRequestURI(),
                exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody(exception.getMessage(), req));
    }

    /**
     * Maps bean validation errors on request bodies to HTTP 400 Bad Request.
     * Behavior:
     * - Aggregates field errors into a list of "field: message".
     * - Returns a JSON array of messages.
     *
     * @param e MethodArgumentNotValidException produced by Bean Validation on @Valid payloads
     * @return ResponseEntity with status 400 and a list of readable validation messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidation(MethodArgumentNotValidException e,
                                                         HttpServletRequest req) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.error("400 Bad Request MethodArgumentNotValidException: [{} {}] {}: {}", req.getMethod(), req.getRequestURI(), e.getClass().getSimpleName(), errors);

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Maps constraint violations on request parameters/path variables to HTTP 400 Bad Request.
     * Behavior:
     * - Returns a generic error message to avoid leaking implementation details.
     *
     * @param e ConstraintViolationException raised for invalid request parameters
     * @return ResponseEntity with status 400 and a short explanatory message
     */
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(jakarta.validation.ConstraintViolationException e,
                                                            HttpServletRequest req) {

        log.error("400 Bad Request ConstraintViolationException: [{} {}] {}: {}", req.getMethod(), req.getRequestURI(), e.getClass().getSimpleName(), "Invalid request parameters");

        return ResponseEntity.badRequest().body("Invalid request parameters");
    }

    /**
     * Maps service unavailability to HTTP 503 Service Unavailable.
     * Behavior:
     * - Returns JSON with message, path, and timestamp.
     *
     * @param ex  DataNotLoadedException indicating the data source is not ready
     * @param req current HTTP request
     * @return ResponseEntity with status 503 and a compact error payload
     */
    @ExceptionHandler(DataNotLoadedException.class)
    public ResponseEntity<Map<String, Object>> handleServiceUnavailable(DataNotLoadedException ex,
                                                                        HttpServletRequest req) {
        log.error("503 Service Unavailable [{} {}] {}: {}", req.getMethod(), req.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorBody(ex.getMessage(), req));
    }

    /**
     * Maps illegal argument errors to HTTP 400 Bad Request.
     * Behavior:
     * - Returns JSON with message, path, and timestamp.
     *
     * @param ex  IllegalArgumentException thrown due to invalid API usage
     * @param req current HTTP request
     * @return ResponseEntity with status 400 and a compact error payload
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex,
                                                                     HttpServletRequest req) {
        log.error("400 Bad Request IllegalArgumentException: [{} {}] {}: {}", req.getMethod(), req.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody(ex.getMessage(), req));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, HttpServletRequest req) {

        log.error("500 Internal Server Error [{} {}] {}", req.getMethod(), req.getRequestURI(), ex.getClass().getSimpleName(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody("Internal error", req));
    }

}