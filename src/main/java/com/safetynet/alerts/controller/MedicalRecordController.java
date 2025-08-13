package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService){
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping("/medicalRecord")
    public MedicalRecord getMedicalRecord(@RequestParam String firstName, @RequestParam String lastName){
        return medicalRecordService.getMedicalRecord(firstName,lastName);
    }

    @PostMapping("/medicalRecord")
    public ResponseEntity<?> addMedicalRecord(@Valid @RequestBody MedicalRecord medicalRecord) {
        MedicalRecord existing = medicalRecordService.getMedicalRecord(medicalRecord.getFirstName(), medicalRecord.getLastName());
        if (existing != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This person already have a medical record");
        }

        MedicalRecord savedMedicalRecord = medicalRecordService.addMedicalRecord(medicalRecord);
        return new ResponseEntity<>(savedMedicalRecord, HttpStatus.CREATED);
    }

    @PutMapping(value = "/medicalRecord/{firstName}/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MedicalRecord> updateMedicalRecord(
            @PathVariable String firstName,
            @PathVariable String lastName,
            @Valid @RequestBody MedicalRecord medicalRecord){
        MedicalRecord updatedMedicalRecord = medicalRecordService.updateMedicalRecord(firstName, lastName, medicalRecord);
        return ResponseEntity.ok(updatedMedicalRecord);
    }
}
