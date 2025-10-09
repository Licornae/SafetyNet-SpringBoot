package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FirestationCoverageDTO;

/**
 * Service for the /firestation endpoint use case:
 * given a station number, return the coverage (persons and counts of adults/children).
 */
public interface FireStationQueryService {

    /**
     * Compute the coverage for the provided station.
     * - 200 OK when the station covers resident.
     * - 400 Bad Request when the station number is blank or empty.
     * - 404 Not Found StationNotFoundException the station doesn't exist.
     * - 404 Not Found AddressNotFoundException if no address is mapped to the station
     *
     * @param stationNumber fire station number (raw string, must not be blank)
     * @return FirestationCoverageDTO : details for the covered person and number of adults and children
     */
    FirestationCoverageDTO getCoverageByStation(String stationNumber);
}
