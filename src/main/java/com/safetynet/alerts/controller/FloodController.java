package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.FloodHouseholdDTO;
import com.safetynet.alerts.service.FloodService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * REST controller exposing the /flood/stations endpoint.
 * Endpoint:
 * - GET /flood/stations?stations=<list of station_numbers>
 * Returns persons with phone numbers, age and medical record order by addresses for the given stations.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class FloodController {

    private final FloodService floodService;

    /**
     * Get persons with medical record for the provided station numbers.
     *
     * @param stationsParam, must not be blank, could be a list
     * @return 200 OK
     */
    @GetMapping("/flood/stations")
    public ResponseEntity<List<FloodHouseholdDTO>> getHouseholdsByStations(@RequestParam("stations") @NotBlank String stationsParam) {
        log.info("GET /flood/stations called with station(s) ='{}'", stationsParam);

        List<String> stations = Arrays.stream(stationsParam.split(","))
                .map(String::trim)
                .toList();

        if (stations.isEmpty()) {
            log.warn("No valid station numbers parsed from '{}'", stationsParam);
            throw new IllegalArgumentException("stations must contain at least one valid station number");
        }

        List<FloodHouseholdDTO> result = floodService.getHouseholdsByStations(stations);
        log.info("Returning {} household group(s) for {} station(s)", result.size(), stations.size());

        return ResponseEntity.ok(result);
    }
}
