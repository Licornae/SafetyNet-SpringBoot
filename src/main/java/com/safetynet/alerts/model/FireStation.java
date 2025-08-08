package com.safetynet.alerts.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FireStation {

    @NotBlank(message = "Adresse obligatoire")
    private String address;

    @NotBlank (message = "Numéro de station obligatoire")
    private String station;

}
