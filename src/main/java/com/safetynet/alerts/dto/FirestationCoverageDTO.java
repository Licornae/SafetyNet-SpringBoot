package com.safetynet.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO representing the coverage of a fire station.
 * Fields:
 * - station (int): station number.
 * - persons (List<PersonDTO>): persons covered (name, age, address, phone).
 * - adults (int): number of adults in the covered area.
 * - children (int): number of children in the covered area.
 * Usage:
 * - Returned as the response body for GET /firestation?stationNumber={n}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirestationCoverageDTO {
    private int station;
    private List<PersonDTO> persons;
    private int adults;
    private int children;
}
