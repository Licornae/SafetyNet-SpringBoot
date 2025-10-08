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
     * - 200 OK with FireDTO when the address is covered by a station and residents exist.
     * - 404 Not Found when the address is unknown (no station) or no resident found.
     * - 400 Bad Request when the address is blank.
     * @param address non-blank address
     * @return 200 OK
     */
    @GetMapping(value = "/fire", params = "address")
    public ResponseEntity<FireDTO> getResidentsByAddress(
            @RequestParam @NotBlank String address) {
        return ResponseEntity.ok(fireService.getResidentsByAddress(address));
    }
}
