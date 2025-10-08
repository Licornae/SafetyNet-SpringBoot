package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.ChildAlertDTO;
import com.safetynet.alerts.service.ChildAlertService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing the /childAlert endpoint.
 * Returns children (<= 18 years) living at a given address and their family members.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ChildAlertController {

    private final ChildAlertService childAlertService;

    /**
     * Get children and family members for the provided address.
     *
     * @param address (must not be blank)
     * @return 200 OK with:
     *         - an empty string "" when the address is known but no children live there
     *         - a JSON array of ChildAlertDTO otherwise
     */
    @GetMapping("/childAlert")
    public ResponseEntity<?> getChildAlertByAddress(@RequestParam("address") @NotBlank String address){
        log.info("GET /childAlert called with address='{}'", address);

        List<ChildAlertDTO> result = childAlertService.getChildrenByAddress(address);

        if (result.isEmpty()) {
            log.debug("No children found at address='{}' -> returning empty body", address);
            return ResponseEntity.ok("");
        }
        log.info("Found {} child(ren) at address='{}'", result.size(), address);
        return ResponseEntity.ok(result);
    }
}
