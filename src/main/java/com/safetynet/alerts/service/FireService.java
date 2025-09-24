package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FireDTO;

public interface FireService {
    FireDTO getResidentsByAddress(String address);
}
