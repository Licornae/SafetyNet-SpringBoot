package com.safetynet.alerts.serviceIT;

import com.safetynet.alerts.dto.EmailDTO;
import com.safetynet.alerts.exception.CityNotFoundException;
import com.safetynet.alerts.repository.DataRepository;
import com.safetynet.alerts.service.CommunityEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CommunityEmailServiceImplTestIT {

    @Autowired
    DataRepository dataRepository;

    @Autowired
    CommunityEmailService communityEmailService;

    @BeforeEach
    public void reload() {
        dataRepository.reloadData();
    }

    @Test
    public void getEmailsByCity_Culver_ReturnsEmails() {
        List<EmailDTO> emails = communityEmailService.getEmailsByCity("Culver");
        assertNotNull(emails, "Result should not be null");
        assertFalse(emails.isEmpty(), "Culver should return at least one email");

        assertTrue(emails.stream().anyMatch(email -> email.equals("jaboyd@email.com")
                        || email.equals("john@email.com")
                        || email.equals("jacob@email.com")),
                "At least one known Culver email should be present");
    }

    @Test
    public void getEmailsByCity_Unknown_ThrowsCityNotFound() {
        assertThrows(CityNotFoundException.class,
                () -> communityEmailService.getEmailsByCity("UnknownCity"),
                "Unknown city should throw CityNotFoundException");
    }

    @Test
    public void getEmailsByCity_blank_throws() {
        assertThrows(IllegalArgumentException.class, () -> communityEmailService.getEmailsByCity(" "));
        assertThrows(IllegalArgumentException.class, () -> communityEmailService.getEmailsByCity(null));
    }

}
