package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FireDTO;

/**
 * Service for the /fire endpoint use case:
 * given an address, return the covering station and the residents with medical info.
 */
public interface FireService {

    /**
     * Collect residents and covering station for the provided address.
     * - 200 OK when the address is covered by a station and residents exist.
     * - 404 Not Found AddressNotFoundException when no station covers the address.
     * - 404 Not Found ResidentsNotFoundException when the address is covered but no residents live there.
     * - 400 Bad Request when the address is blank/empty.
     *
     * @param address raw address
     * @return FireDTO containing the station number and the list of residents
     */
    FireDTO getResidentsByAddress(String address);
}
