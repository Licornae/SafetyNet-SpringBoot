package com.safetynet.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FloodHouseholdDTO {
    private String station;
    private String address;
    private List<PersonMedicalInfoDTO> residents;
}
