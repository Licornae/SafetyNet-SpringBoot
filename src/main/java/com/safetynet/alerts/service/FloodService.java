package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FloodHouseholdDTO;

import java.util.List;

/**
 * Service for the /flood/stations endpoint use case:
 * given one or several fire station numbers, return households grouped by address,
 * including residents with phone, age, medications and allergies.
 */
public interface FloodService {
    /**
     * Returns households grouped by address for the provided fire station numbers.
     * Contract:
     * - IllegalArgumentException when the list is null/empty or contains only blank values
     * - DataNotLoadedException when the dataset is not available
     * - AddressNotFoundException when no address is covered by the given stations
     *   or when covered addresses have no residents
     *
     * @param stations list of station numbers
     * @return list of FloodHouseholdDTO
     */
    List<FloodHouseholdDTO> getHouseholdsByStations(List<String> stations);
}
