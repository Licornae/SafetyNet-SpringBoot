package com.safetynet.alerts.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mapping between an address and a fire station number.
 * Each instance represents one mapping entry from the dataset.
 * NotBlank constraints enforce minimal data integrity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FireStation {

    @NotBlank(message = "Address required")
    private String address;

    @NotBlank (message = "Station number required")
    private String station;

}
