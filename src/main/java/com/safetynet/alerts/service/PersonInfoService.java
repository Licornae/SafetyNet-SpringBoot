package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonInfoDTO;

import java.util.List;

public interface PersonInfoService {
    List<PersonInfoDTO> getPersonInfoByLastName(String lastName);
}
