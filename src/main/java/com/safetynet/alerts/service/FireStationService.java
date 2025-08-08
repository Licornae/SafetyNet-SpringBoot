package com.safetynet.alerts.service;

import com.safetynet.alerts.model.FireStation;

public interface FireStationService {
    FireStation getFireStation(String address);
    FireStation addFireStation(FireStation fireStation);
}
