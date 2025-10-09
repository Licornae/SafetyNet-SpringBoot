package com.safetynet.alerts.controllerIT;


import com.safetynet.alerts.controller.FireStationQueryController;
import com.safetynet.alerts.dto.FirestationCoverageDTO;
import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.exception.StationNotFoundException;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@AutoConfigureMockMvc
public class FireStationQueryControllerTestIT {

    @Autowired
    private FireStationQueryController controller;

    @Autowired
    private DataRepository dataRepository;

    @BeforeEach
    void reload() {
        dataRepository.reloadData();
    }

    @Test
    public void getCoverageByStation_WithRealDataset_Returns200() {
        ResponseEntity<FirestationCoverageDTO> response = controller.getCoverageByStation("1");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();

        FirestationCoverageDTO body = response.getBody();
        assertThat(body.getPersons()).isNotNull().isNotEmpty();

        PersonDTO first = body.getPersons().get(0);
        assertThat(first.getFirstName()).isNotBlank();
        assertThat(first.getLastName()).isNotBlank();
        assertThat(first.getAddress()).isNotBlank();
        assertThat(first.getPhone()).isNotBlank();

        assertThat(body.getAdults()).isGreaterThanOrEqualTo(0);
        assertThat(body.getChildren()).isGreaterThanOrEqualTo(0);
        assertThat(body.getChildren() + body.getAdults())
                .isEqualTo(body.getPersons().size());
    }

    @Test
    void unknownStation_throwsStationNotFoundException() {
        assertThatThrownBy(() -> controller.getCoverageByStation("999"))
                .isInstanceOf(StationNotFoundException.class);
    }
}
