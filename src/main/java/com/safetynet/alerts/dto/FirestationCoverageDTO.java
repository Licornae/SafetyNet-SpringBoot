package com.safetynet.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class FirestationCoverageDTO {
    private List<PersonDTO> persons;
    private int adults;
    private int children;
}
