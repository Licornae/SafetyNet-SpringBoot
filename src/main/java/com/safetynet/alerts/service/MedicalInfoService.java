package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import java.util.List;


/**
 * Utility service for accessing a person's medical information from the dataset.
 * Retrieve a medical record by identity (first name/last name).
 */
public interface MedicalInfoService {

    /**
     * Finds a medical record by exact first and last name.
     * Contract:
     * - Returns null if the DataContainer is not loaded, if the medical records list is null,
     *   if any parameter is null, or if no record matches.
     *
     * @param firstName exact first name
     * @param lastName  exact last name
     * @return the matching MedicalRecord, or null if not found/invalid inputs
     */
    MedicalRecord findMedicalRecordByName(String firstName, String lastName);

    /**
     * Computes a person's age from the associated medical record.
     * Contract:
     * - Returns null if the person is null, if the medical record is missing,
     *   or if the birthdate is invalid.
     *
     * @param person target person
     * @return age in years
     */
    Integer getAgeFor(Person person);

    /**
     * Retrieves the list of medications for a person from the medical record.
     * Contract:
     * - Returns an empty list if the person or record is missing, or if the list itself is absent.
     *
     * @param person target person
     * @return a list of medications, can be empty
     */
    List<String> getMedicationsFor(Person person);


    /**
     * Retrieves the list of allergies for a person from the medical record.
     * Contract:
     * - Returns an empty list if the person or record is missing, or if the list itself is absent.
     *
     * @param person target person
     * @return a list of allergies, can be empty
     */
    List<String> getAllergiesFor(Person person);
}
