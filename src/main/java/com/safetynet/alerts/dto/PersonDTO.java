package com.safetynet.alerts.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight person view enriched with computed age.
 * Fields:
 * - firstName (String): first name.
 * - lastName (String): last name.
 * - age (int): computed age.
 * - address (String): postal address.
 * - phone (String): phone number.
 * Usage:
 * - Element of FirestationCoverageDTO.persons.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
    private String firstName;
    private String lastName;
    private int age;
    private String address;
    private String phone;
}
