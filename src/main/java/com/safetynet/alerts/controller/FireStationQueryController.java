package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.FirestationCoverageDTO;
import com.safetynet.alerts.service.FireStationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FireStationQueryController {

    private final FireStationQueryService queryService;

    @GetMapping(value = "/firestation", params = "stationNumber")
    public ResponseEntity<FirestationCoverageDTO> getCoverageByStation(@RequestParam String stationNumber) {
        return ResponseEntity.ok(queryService.getCoverageByStation(stationNumber));
    }
}
