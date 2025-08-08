package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                    .body("Aucune caserne associée à cette adresse");
        }
        return ResponseEntity.ok(fireStation);
    }

    @PostMapping("/firestation")
    public ResponseEntity<?> addFireStation(@Valid @RequestBody FireStation fireStation) {
        FireStation existing = fireStationService.getFireStation(fireStation.getAddress());
        if (existing != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cette adresse renseigne déjà une station");
        }

        FireStation savedFireStation = fireStationService.addFireStation(fireStation);
        return new ResponseEntity<>(savedFireStation, HttpStatus.CREATED);
    }
}
