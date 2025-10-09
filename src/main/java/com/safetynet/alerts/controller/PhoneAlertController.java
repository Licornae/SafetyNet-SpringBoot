package com.safetynet.alerts.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.service.PhoneAlertService;

import java.util.List;

/**
 * REST controller exposing the /phoneAlert endpoint.
 * Endpoint:
 * - GET /phoneAlert?firestation={stationNumber}
 * Returns array of distinct phone numbers covered by the given station.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class PhoneAlertController {

    private final PhoneAlertService phoneAlertService;

    /**
     * Returns all distinct phone numbers for residents covered by the provided fire station.
     * Responses:
     * - 404 Not Found when the station number is unknown.
     * - 400 Bad Request when the station number is blank.
     * @param station firestation number, must not be blank
     * @return 200 OK with a JSON array of phone numbers (possibly empty, but never null)
     */
    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhones(@RequestParam("firestation") @NotBlank String station) {
        log.info("GET /phoneAlert called with firestation='{}'", station);

        List<String> phones = phoneAlertService.getPhonesByFirestation(station);
        log.debug("Returning {} phone(s) for firestation='{}'", phones.size(), station);

        return ResponseEntity.ok(phones);
    }
}
