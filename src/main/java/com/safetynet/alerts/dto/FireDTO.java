package com.safetynet.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing the station covering an address and the associated residents.
 * Fields:
 * - station (String): number of the fire station covering the address.
 * - residents (List<PersonMedicalInfoDTO>): residents with phone, age, medications, and allergies.
 * Usage:
 * - Response body for GET /fire?address={value}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FireDTO {
    private String station;
    private List<PersonMedicalInfoDTO> residents;
}
