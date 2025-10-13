package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.ChildAlertDTO;

import java.util.List;

/**
 * Service for the /childAlert endpoint use case:
 * given an address, return the covering children with age and their family members.
 */
public interface ChildAlertService {

    /**
     * Returns children and their family members for a given address.
     * Contract:
     * - IllegalArgumentException when address is blank/invalid
     * - AddressNotFoundException when the address is not present in the dataset
     * - Returns an empty list when address is known but contains adults only
     *
     * @param address raw address
     * @return List of ChildAlertDTO, possibly empty
     */
    List<ChildAlertDTO> getChildrenByAddress(String address);
}
