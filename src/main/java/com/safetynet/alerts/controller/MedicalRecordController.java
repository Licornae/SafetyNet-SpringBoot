package com.safetynet.alerts.controller;

import com.safetynet.alerts.exception.MedicalRecordNotFoundException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that exposes CRUD operations for MedicalRecord.
 * Endpoint:
 * - POST /medicalRecord — Create a new medical record
 * - PUT /medicalRecord/{firstName}/{lastName} — Update an existing medical record
 * - DELETE /medicalRecord/{firstName}/{lastName} — Delete an existing medical record
 * For updates, the path variables must match the identifiers in the body when provided otherwise an IllegalArgumentException is thrown.
 * Deletion returns HTTP 204 when successful
 * If the record does not exist, a MedicalRecordNotFoundException is thrown.
 */
@Slf4j
@Validated
@RestController
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService){
        this.medicalRecordService = medicalRecordService;
    }

    /**
     * Creates a new medical record.
     * Method: POST
     * Path: /medicalRecord
     * Response: 201 Created with the MedicalRecord in the response body on success.
     *
     * @param medicalRecord the record to create must be valid and contain required fields
     * @return ResponseEntity with status 201 and the created MedicalRecord
     */

    @PostMapping(value = "/medicalRecord", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addMedicalRecord(@Valid @RequestBody MedicalRecord medicalRecord) {
        log.info("POST /medicalRecord for '{} {}'", medicalRecord.getFirstName(), medicalRecord.getLastName());

        MedicalRecord savedMedicalRecord = medicalRecordService.addMedicalRecord(medicalRecord);
        log.info("Medical record created for '{} {}'", savedMedicalRecord.getFirstName(), savedMedicalRecord.getLastName());

        return new ResponseEntity<>(savedMedicalRecord, HttpStatus.CREATED);
    }

    /**
     * Updates an existing medical record identified by first and last name.
     * Method: PUT
     * Path: /medicalRecord/{firstName}/{lastName}
     * Response: 200 OK with the updated MedicalRecord
     *
     * @param firstName the first name identifying the medical record must not be blank
     * @param lastName  the last name identifying the medical record must not be blank
     * @param medicalRecord the updated payload must be valid
     * @return ResponseEntity with status 200 and the updated MedicalRecord
     */
    @PutMapping(value = "/medicalRecord/{firstName}/{lastName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MedicalRecord> updateMedicalRecord(
            @PathVariable @NotBlank String firstName,
            @PathVariable @NotBlank String lastName,
            @Valid @RequestBody MedicalRecord medicalRecord){
        log.info("PUT /medicalRecord/{}/{}", firstName, lastName);

        //path and body consistency check
        if ((medicalRecord.getFirstName() != null && !medicalRecord.getFirstName().equals(firstName))
                || (medicalRecord.getLastName() != null && !medicalRecord.getLastName().equals(lastName))) {
            throw new IllegalArgumentException("Path {firstName,lastName} must match body.");
        }

        MedicalRecord updatedMedicalRecord = medicalRecordService.updateMedicalRecord(firstName, lastName, medicalRecord);
        log.info("Medical record updated for '{} {}'", firstName, lastName);

        return ResponseEntity.ok(updatedMedicalRecord);
    }

    /**
     * Deletes a medical record identified by first and last name.
     * Method: DELETE
     * Path: /medicalRecord/{firstName}/{lastName}
     * Response:
     * - 204 No Content when the record is successfully deleted.
     * - 404 Not Found MedicalRecordNotFoundException when no record matches the provided identifiers.
     *
     * @param firstName the first name identifying the medical record must not be blank
     * @param lastName  the last name identifying the medical record must not be blank
     * @return ResponseEntity with status 204 if deletion succeeded
     */
    @DeleteMapping("/medicalRecord/{firstName}/{lastName}")
    public ResponseEntity<Void> deleteMedicalRecord(
            @PathVariable String firstName,
            @PathVariable String lastName) {
        log.info("DELETE /medicalRecord/{}/{}", firstName, lastName);

        boolean deleted = medicalRecordService.deleteMedicalRecord(firstName.trim(), lastName.trim());
        if(deleted) {
            log.info("Deleted medical records for '{} {}'", firstName, lastName);
            return ResponseEntity.noContent().build();
        }
        log.info("Deletion impossible, medical records not found for: {} {}", firstName, lastName);
        return ResponseEntity.notFound().build();
    }
}
