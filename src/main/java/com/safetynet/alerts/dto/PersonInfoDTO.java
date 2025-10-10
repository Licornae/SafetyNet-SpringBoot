package com.safetynet.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing detailed information about a person.
 * Fields:
 * - firstName (String): person's first name.
 * - lastName (String): person's last name.
 * - address (String): current street address.
 * - age (Integer): computed age based on the medical record birthdate.
 * - email (String): email.
 * - medications (List<String>): list of medications.
 * - allergies (List<String>): list of known allergies.
 * Usage:
 * - Returned as part of the response for GET /personInfo?firstName={}&lastName={}.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonInfoDTO {
    private String firstName;
    private String lastName;
    private String address;
    private Integer age;
    private String email;
    private List<String> medications;
    private List<String> allergies;
}
