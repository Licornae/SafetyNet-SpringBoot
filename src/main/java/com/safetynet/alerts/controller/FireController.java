package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.FireDTO;
import com.safetynet.alerts.service.FireService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the /fire endpoint.
 * Endpoint:
 * - GET /fire?address={value}
 * Returns the fire station covering the given address and the list of residents
 * with their phone, age, medications and allergies.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class FireController {
    private final FireService fireService;

    /**
     * Collect residents and covering station for the provided address.
     * Responses:
     * - 404 Not Found when the address is unknown (no station) or no resident found.
     * - 400 Bad Request when the address is blank.
     * @param address non-blank address
     * @return 200 OK
     */
    @GetMapping(value = "/fire", params = "address")
    public ResponseEntity<FireDTO> getResidentsByAddress(@RequestParam @NotBlank String address) {
        log.info("GET /fire called with address='{}'", address);

        FireDTO result = fireService.getResidentsByAddress(address);
        log.debug("FireDTO payload for address='{}': {} residents", address, result.getResidents().size());

        return ResponseEntity.ok(result);
    }

}
