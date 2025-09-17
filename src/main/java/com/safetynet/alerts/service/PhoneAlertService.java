package com.safetynet.alerts.service;

import java.util.List;

public interface PhoneAlertService {
    List<String> getPhonesByFirestation(String stationNumber);
}
