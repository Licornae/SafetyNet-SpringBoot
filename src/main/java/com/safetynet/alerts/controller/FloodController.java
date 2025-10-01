package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.FloodHouseholdDTO;
import com.safetynet.alerts.service.FloodService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class FloodController {

    private final FloodService floodService;

    @GetMapping("/flood/stations")
    public ResponseEntity<List<FloodHouseholdDTO>> getHouseholdsByStations(@RequestParam("stations") @NotBlank String stationsParam) {

        List<String> stations = Arrays.stream(stationsParam.split(","))
                .map(String::trim)
                .toList();

        List<FloodHouseholdDTO> result = floodService.getHouseholdsByStations(stations);

        return ResponseEntity.ok(result);
    }
}
