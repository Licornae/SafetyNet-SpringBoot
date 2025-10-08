package com.safetynet.alerts.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight view of a person enriched with computed age.
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
