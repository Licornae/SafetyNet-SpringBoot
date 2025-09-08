package com.safetynet.alerts.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FireStation {

    @NotBlank(message = "Address required")
    private String address;

    @NotBlank (message = "Station number required")
    private String station;

}
