package com.safetynet.alerts.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing a person's basic and medical information.
 * Fields:
 * - firstName (String): person's first name.
 * - lastName (String): person's last name.
 * - phone (String): phone number.
 * - age (int): computed age?
 * - medications (List<String>): list of medications.
 * - allergies (List<String>): list of known allergies.
 * Usage:
 * - Returned within residents lists for:
 *   - GET /fire?address={address}.
 *   - GET /flood/stations?stations={s1,s2,...}.
 * - Age is computed at query time from the medical record; medications and allergies
 *   are read from the medical record.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonMedicalInfoDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private int age;
    private List<String> medications;
    private List<String> allergies;
}
