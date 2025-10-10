package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.DuplicateMedicalRecordException;
import com.safetynet.alerts.model.MedicalRecord;

/**
 * Service API for managing MedicalRecord entities.
 * Responsibilities: Retrieve, create, update, and delete medical records identified by first and last name.</li>
 */
public interface MedicalRecordService {

    /**
     * Returns the medical record for a given person, or null when not found.
     *
     * @param firstName the person's first name must not be blank
     * @param lastName  the person's last name must not be blank
     * @return the matching MedicalRecord, or null if none exists
     */
    MedicalRecord getMedicalRecord(String firstName, String lastName);

    /**
     * Creates a new medical record.
     * Contract: Implementations should prevent duplicates for the same {firstName, lastName}.
     *
     * @param medicalRecord the record to create must not be blank and should contain required fields
     * @return MedicalRecord
     * @throws DuplicateMedicalRecordException if the medical record already exists
     */
    MedicalRecord addMedicalRecord(MedicalRecord medicalRecord);

    /**
     * Updates an existing medical record identified by first and last name.
     *
     * @param firstName the first name identifying the record to update must not be blank
     * @param lastName the last name identifying the record to update must not be blank
     * @param updatedMedicalRecord payload containing fields to replace must not be blank
     * @return the updated MedicalRecord
     */
    MedicalRecord updateMedicalRecord(String firstName, String lastName, MedicalRecord updatedMedicalRecord);

    /**
     * Deletes the medical record identified by first and last name.
     *
     * @param firstName the first name identifying the record must not be blank
     * @param lastName  the last name identifying the record; must not be blank
     * @return True when a record was deleted, False when no matching record exists
     */
    boolean deleteMedicalRecord(String firstName, String lastName);
}
