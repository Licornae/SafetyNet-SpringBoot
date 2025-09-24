package com.safetynet.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FireDTO {
    private String station;
    private List<PersonMedicalInfoDTO> residents;
}
