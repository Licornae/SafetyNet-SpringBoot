package com.safetynet.alerts.controllerIT;

import com.safetynet.alerts.controller.PhoneAlertController;
import com.safetynet.alerts.exception.StationNotFoundException;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class PhoneAlertControllerIT {

    @Autowired
    private PhoneAlertController controller;

    @Autowired
    private DataRepository dataRepository;

    @BeforeEach
    public void reload() {
        dataRepository.reloadData();
    }

    @Test
    public void existingStation_ReturnsDistinctPhones() {

        String station = "3";

        ResponseEntity<List<String>> response = controller.getPhones(station);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void unknownStation_ThrowsStationNotFoundException() {
        assertThatThrownBy(() -> controller.getPhones("999"))
                .isInstanceOf(StationNotFoundException.class);
    }

}
