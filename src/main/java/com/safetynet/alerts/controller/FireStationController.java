package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Validated
@RestController
public class FireStationController {

    private final FireStationService fireStationService;

    @Autowired
    public FireStationController( FireStationService fireStationService){
        this.fireStationService = fireStationService;
    }

    @GetMapping("/firestation")
    public ResponseEntity<?> getStationByAddress(@RequestParam String address) {
        FireStation fireStation = fireStationService.getFireStation(address);

        if (fireStation == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No fire station associated with this address");
        }
        return ResponseEntity.ok(fireStation);
    }

    @PostMapping("/firestation")
    public ResponseEntity<?> addFireStation(@Valid @RequestBody FireStation fireStation) {
        FireStation existing = fireStationService.getFireStation(fireStation.getAddress());
        if (existing != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This address already refers to a station");
        }
        FireStation savedFireStation = fireStationService.addFireStation(fireStation);
        return new ResponseEntity<>(savedFireStation, HttpStatus.CREATED);
    }

    @PutMapping("/firestation/{address}")
    public ResponseEntity<FireStation> updateStationAddress(
            @PathVariable @NotBlank String address,
            @Valid @RequestBody FireStation updatedFireStationAddress) {
        FireStation updatedStation = fireStationService.updateStationAddress(address, updatedFireStationAddress);
        return ResponseEntity.ok(updatedStation);
    }

    @DeleteMapping("/firestation")
    public ResponseEntity<Void> deleteFireStationByAddress(@RequestParam String address) {
        boolean deleted = fireStationService.deleteFireStationByAddress(address);
        if(deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE by station
    @DeleteMapping(value = "/firestation", params = "station")
    public ResponseEntity<?> deleteFireStationsByStation(
            @RequestParam String station,
            @RequestParam(defaultValue = "false") boolean dryRun,
            @RequestParam(required = false) String confirm) {

        int count = fireStationService.countByStation(station);

        if (dryRun) {
            return ResponseEntity.ok(Map.of("station", station, "count", count));
        }

        if (!"YES".equalsIgnoreCase(confirm)) {
            return ResponseEntity.badRequest()
                    .body("Dangerous operation. Run dryRun first to see impact, then call with confirm=YES to proceed.");
        }

        boolean deleted = fireStationService.deleteFireStationsByStation(station);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
