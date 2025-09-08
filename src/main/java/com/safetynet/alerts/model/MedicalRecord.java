package com.safetynet.alerts.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

    @NotBlank(message = "First name required")
    private String firstName;

    @NotBlank (message = "Last name required")
    private String lastName;

    @NotBlank (message = "Birthdate required")
    private String birthdate;

    private List<String> medications;
    private List<String> allergies;
}
