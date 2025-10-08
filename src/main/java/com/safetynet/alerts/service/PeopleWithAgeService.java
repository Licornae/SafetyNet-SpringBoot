package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonDTO;

import java.util.List;

/**
 * Computes age for all persons from repository and exposes a flat list.
 */
public interface PeopleWithAgeService {

    /**
     * Build a list of PersonDTO with age computed from medical records
     *
     * @return list of PersonDTO
     */
    List<PersonDTO> listAll();
}
