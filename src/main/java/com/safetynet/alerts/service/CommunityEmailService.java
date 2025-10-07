package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.EmailDTO;

import java.util.List;

public interface CommunityEmailService {
    List<EmailDTO> getEmailsByCity(String city);
}
