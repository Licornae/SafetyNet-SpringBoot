package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FloodHouseholdDTO;

import java.util.List;

public interface FloodService {
    List<FloodHouseholdDTO> getHouseholdsByStations(List<String> stations);
}
