package com.safetynet.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing household covered by one or more fire stations.
 * Fields:
 * - station (String): station number covering the address.
 * - address (String): street address of the household.
 * - residents (List<PersonMedicalInfoDTO>): residents of the household, each with
 *   phone, computed age, medications, and allergies.
 * Usage:
 * - Returned as part of the response for GET /flood/stations?stations={s1,s2,...}.
 * - Results are grouped by address each instance represents a single household.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FloodHouseholdDTO {
    private String station;
    private String address;
    private List<PersonMedicalInfoDTO> residents;
}
