package com.safetynet.alerts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

/**
 * Domain model representing a person stored in the dataset.
 * Validation:
 * - All main identity and contact fields are required (NotBlank).
 * - Email is optional but validated when provided.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @NotBlank (message = "First name required")
    private String firstName;

    @NotBlank (message = "Last name required")
    private String lastName;

    @NotBlank (message = "Address required")
    private String address;

    @NotBlank (message = "City required")
    private String city;

    @NotBlank (message = "Zip required")
    private String zip;

    @NotBlank (message = "Phone number required")
    private String phone;

    private String email;
}
