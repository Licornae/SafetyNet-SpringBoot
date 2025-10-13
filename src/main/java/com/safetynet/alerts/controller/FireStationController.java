package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import java.util.Map;

/**
 * REST controller exposing CRUD operations for Firestation resources.
 * Endpoint:
 * - POST /firestation — Create a new firestation
 * - PUT /firestation/{address} — Update an existing firestation
 * - DELETE /firestation?station={stationNumber}&confirm=YES — Delete an existing firestation
 */
@Slf4j
@Validated
@RestController
public class FireStationController {

    private final FireStationService fireStationService;

    @Autowired
    public FireStationController( FireStationService fireStationService){
        this.fireStationService = fireStationService;
    }

    /**
     * Creates a new firestation.
     * Method: POST
     * Path: /firestation
     * Response:
     * - 201 Created on success.
     * - 400 Bad Request if the payload is invalid.
     * - 409 Conflict if the station already exists.
     *
     * @param fireStation the station to create must be valid
     * @return ResponseEntity with status 201 and body with created station
     */
    @PostMapping("/firestation")
    public ResponseEntity<?> addFireStation(@Valid @RequestBody FireStation fireStation) {
        log.info("POST /firestation - creating mapping: {}", fireStation);

        FireStation saved = fireStationService.addFireStation(fireStation);
        log.info("Firestation mapping created: address='{}', station='{}'", saved.getAddress(), saved.getStation());

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Updates an existing firestation identified by address.
     * Method: PUT
     * Path: firestation/{address}
     * Response: 200 OK with the updated firestation
     *
     * @param address the address must not be blank
     * @param updatedFireStationAddress updated payload must be valid
     * @return ResponseEntity status 200 and the updated firestation
     */
    @PutMapping("/firestation/{address}")
    public ResponseEntity<FireStation> updateStationAddress(
            @PathVariable @NotBlank String address,
            @Valid @RequestBody FireStation updatedFireStationAddress) {
        log.info("PUT /firestation/{} - payload: {}", address, updatedFireStationAddress);

        //path and body consistency check
        if (updatedFireStationAddress.getAddress() != null && !address.equals(updatedFireStationAddress.getAddress())) {
            log.warn("Path/body mismatch on address. path='{}', body='{}'", address, updatedFireStationAddress.getAddress());
            throw new IllegalArgumentException("Path/body mismatch: address must equal '" + address + "'");
        }

        FireStation updated = fireStationService.updateStationAddress(address, updatedFireStationAddress);
        log.info("Firestation mapping updated: address='{}' -> station='{}'", updated.getAddress(), updated.getStation());

        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a firestation mapping by address.
     * Method: DELETE
     * Path: /firestation?address={address}
     * Response:
     * - 204 No Content when the mapping is successfully deleted.
     * - 404 Not Found if no mapping exists for the provided address.
     *
     * @param address the address to delete must not be blank
     * @return ResponseEntity with status 204 if deletion succeeded
     */
    @DeleteMapping("/firestation")
    public ResponseEntity<Void> deleteFireStationByAddress(@RequestParam String address) {
        log.info("DELETE /firestation?address={}", address);

        fireStationService.deleteFireStationByAddress(address);
        log.info("Deleted firestation mapping for address='{}'", address);

        return ResponseEntity.noContent().build();
    }


    /**
     * Deletes firestation mappings by station number.
     * Method: DELETE
     * Path: /firestation?station={station}[&dryRun=true|false][&confirm=YES]
     * Behavior:
     * - dryRun=true returns the number of mappings that would be deleted.
     * - dryRun=false and confirm=YES permit deletion.
     * Response:
     * - 200 OK with {"station","count"} when dryRun=true.
     * - 400 Bad Request if confirm is missing/invalid when dryRun=false.
     * - 204 No Content when deletion succeeds.
     * - 404 Not Found if no mapping exists for the station.
     *
     * @param station the station identifier must not be blank
     * @param dryRun  when true, returns only the impact summary
     * @param confirm must be "YES" to proceed when dryRun=false
     * @return ResponseEntity with 200 (dryRun) or 204 (deleted)
     */
    @DeleteMapping(value = "/firestation", params = "station")
    public ResponseEntity<?> deleteFireStationsByStation(
            @RequestParam String station,
            @RequestParam(defaultValue = "false") boolean dryRun,
            @RequestParam(required = false) String confirm) {
        log.info("DELETE /firestation?station={}&dryRun={}&confirm={}", station, dryRun, confirm);

        int count = fireStationService.countByStation(station);

        if (dryRun) {
            log.info("Dry-run deletion report for station='{}': {} mapping(s) impacted", station, count);
            return ResponseEntity.ok(Map.of("station", station, "count", count));
        }

        if (!"YES".equalsIgnoreCase(confirm)) {
            log.warn("Dangerous operation rejected for station='{}': confirm missing or not 'YES'", station);
            throw new IllegalArgumentException("Dangerous operation. Run dryRun first to see impact, then call with confirm=YES to proceed.");
        }

        fireStationService.deleteFireStationsByStation(station);

        log.warn("Deleted {} firestation mapping(s) for station='{}'", count, station);
        return ResponseEntity.noContent().build();
    }
}
