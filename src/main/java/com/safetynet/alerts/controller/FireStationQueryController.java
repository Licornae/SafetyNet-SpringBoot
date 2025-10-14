package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.FirestationCoverageDTO;
import com.safetynet.alerts.service.FireStationQueryService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing queries related to fire stations.
 * Endpoint:
 * - GET /firestation?stationNumber={n}
 * Returns the coverage (persons, number of adults and children) for the requested station.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class FireStationQueryController {

    private final FireStationQueryService queryService;

    @GetMapping(value = "/firestation", params = "stationNumber")
    public ResponseEntity<FirestationCoverageDTO> getCoverageByStation(@RequestParam @NotBlank String stationNumber) {
        log.info("GET /firestation called with stationNumber='{}'", stationNumber);

        FirestationCoverageDTO result = queryService.getCoverageByStation(stationNumber);
        log.info("FirestationCoverageDTO payload for station='{}': {} person(s), {} adult(s), {} child(ren)", stationNumber, result.getPersons().size(), result.getAdults(), result.getChildren());

        return ResponseEntity.ok(result);
    }
}
