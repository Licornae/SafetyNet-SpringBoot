package com.safetynet.alerts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @NotBlank (message = "Prénom obligatoire")
    private String firstName;

    @NotBlank (message = "Nom obligatoire")
    private String lastName;

    @NotBlank (message = "Adresse obligatoire")
    private String address;

    @NotBlank (message = "Ville obligatoire")
    private String city;

    @NotBlank (message = "Code postale obligatoire")
    private String zip;

    @NotBlank (message = "Numéro de téléphone obligatoire")
    private String phone;

    private String email;
}
