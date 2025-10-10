package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonInfoDTO;

import java.util.List;

/**
 * Service for the /personInfolastName endpoint use case:
 * given a last name, return detailed info : identity, address, age, email, medications, allergies
 * for all matching persons.
 */
public interface PersonInfoService {

    /**
     * Returns detailed information for all persons matching the given last name.
     * Contract:
     * - IllegalArgumentException when lastName is blank/invalid
     * - DataNotLoadedException when the dataset is not available
     * - PersonNotFoundException when no person matches the given lastName
     * - Age is computed from the medical record birthdate.
     * - Medications and allergies are read from the medical record.
     *
     * @param lastName raw last name to search
     * @return a non-empty List of PersonInfoDTO
     */
    List<PersonInfoDTO> getPersonInfoByLastName(String lastName);
}
