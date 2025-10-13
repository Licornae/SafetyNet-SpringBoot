package com.safetynet.alerts.service;

import com.safetynet.alerts.model.FireStation;

/**
 * Service API for managing FireStation entities.
 * Contract:
 * - addFireStation: throws DuplicateFireStationException if the address already exists.
 * - updateStationAddress: throws AddressNotFoundException if the address does not exist.
 * - deleteFireStationByAddress: returns true when an address mapping is removed, otherwise throws AddressNotFoundException.
 * - deleteFireStationsByStation: removes all mappings for a station throws StationNotFoundException when none exists.
 * - countByStation: returns the number of mappings for a given station.
 */
public interface FireStationService {

    /**
     * Returns the fire station mapping for a given address, or null when not found.
     *
     * @param address the address must not be blank
     * @return the matching FireStation or null if none exists
     */
    FireStation getFireStation(String address);

    /**
     * Creates a new station/address mapping.
     * Contract: Implementations should prevent duplicates for the same address.
     *
     * @param fireStation the mapping to create must not be null and should contain address and station
     * @return the created FireStation mapping
     */
    FireStation addFireStation(FireStation fireStation);

    /**
     * Updates the station number associated with an existing address.
     *
     * @param address the existing address to update must not be blank
     * @param updatedFireStationAddress payload carrying the new station value must not be null
     * @return the updated FireStation mapping
     */
    FireStation updateStationAddress(String address, FireStation updatedFireStationAddress);

    /**
     * Deletes the mapping for the given address.
     *
     * @param address the address to delete must not be blank
     * @return true if a mapping was deleted
     */
    boolean deleteFireStationByAddress(String address);

    /**
     * Deletes all mappings associated with the given station.
     *
     * @param station the station identifier must not be blank
     * @return true if at least one mapping was deleted
     */
    boolean deleteFireStationsByStation(String station);

    /**
     * Counts how many mappings are associated with the given station.
     *
     * @param station the station identifier must not be blank
     * @return the number of mappings for the station
     */
    int countByStation(String station);
}
