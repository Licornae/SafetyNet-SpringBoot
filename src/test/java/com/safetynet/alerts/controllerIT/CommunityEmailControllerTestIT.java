package com.safetynet.alerts.controllerIT;

import com.safetynet.alerts.controller.CommunityEmailController;
import com.safetynet.alerts.dto.EmailDTO;
import com.safetynet.alerts.exception.CityNotFoundException;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
public class CommunityEmailControllerTestIT {

    @Autowired
    private CommunityEmailController controller;

    @Autowired
    private DataRepository dataRepository;

    @BeforeEach
    public void reload() {
        dataRepository.reloadData();
    }

    @Test
    public void existingCity_ReturnsDistinctEmails() {

        String city = "Culver";

        ResponseEntity<List<EmailDTO>> response = controller.getEmailByCity(city);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull().isNotEmpty();

        List<EmailDTO> emails = response.getBody();

        assertThat(emails).allSatisfy(dto -> assertThat(dto.getEmail()).isNotBlank());
    }

    @Test
    public void unknownCity_ThrowsCityNotFoundException() {
        assertThatThrownBy(() -> controller.getEmailByCity("Unknown City"))
                .isInstanceOf(CityNotFoundException.class);
    }

}
