package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.ChildAlertDTO;

import java.util.List;

public interface ChildAlertService {
    List<ChildAlertDTO> getChildrenByAddress(String address);
}
