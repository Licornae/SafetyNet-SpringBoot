package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.EmailDTO;
import java.util.List;

/**
 * Service for the /communityEmail endpoint use case:
 * given a city, return the distinct list of resident email addresses.
 */
public interface CommunityEmailService {

    /**
     * Returns distinct resident email addresses for a given city.
     * Contract:
     * - IllegalArgumentException when city is blank
     * - CityNotFoundException when the city is not present in the dataset
     * - Returns an empty list when the city exists but no valid emails are recorded
     *
     * @param city city name
     * @return list of distinct EmailDTO, possibly empty
     */
    List<EmailDTO> getEmailsByCity(String city);
}
