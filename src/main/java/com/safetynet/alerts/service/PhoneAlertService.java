package com.safetynet.alerts.service;

import java.util.List;

/**
 * Service for the /phoneAlert endpoint use case:
 * given a station number, return the covering phone numbers.
 */
public interface PhoneAlertService {

    /**
     * Returns distinct phone numbers for residents covered by the provided fire station.
     * - 200 OK when phone numbers are found for the given station number.
     * - 404 Not Found StationNotFoundException when no station found.
     * - 400 Bad Request when the station number is blank/empty.
     * @param stationNumber firestation number, must not be blank
     * @return a list of unique phone numbers
     */
    List<String> getPhonesByFirestation(String stationNumber);
}
