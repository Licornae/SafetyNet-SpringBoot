package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FirestationCoverageDTO;

public interface FireStationQueryService {
    FirestationCoverageDTO getCoverageByStation(String stationNumber);
}
