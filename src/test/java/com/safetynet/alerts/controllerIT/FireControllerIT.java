package com.safetynet.alerts.controllerIT;

import com.safetynet.alerts.controller.FireController;
import com.safetynet.alerts.dto.FireDTO;
import com.safetynet.alerts.dto.PersonMedicalInfoDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;
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
public class FireControllerIT {

    @Autowired
    private FireController controller;

    @Autowired
    private DataRepository dataRepository;

    @BeforeEach
    public void reload() {
        dataRepository.reloadData();
    }

    @Test
    public void existingAddress_ReturnsStationAndResidents() {
        String address = "1509 Culver St";

        ResponseEntity<FireDTO> response = controller.getResidentsByAddress(address);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();

        FireDTO body = response.getBody();
        assertThat(body.getStation()).isEqualTo("3");
        assertThat(body.getResidents()).isNotNull().isNotEmpty();

        PersonMedicalInfoDTO first = body.getResidents().get(0);
        assertThat(first.getFirstName()).isNotBlank();
        assertThat(first.getLastName()).isNotBlank();
        assertThat(first.getPhone()).isNotBlank();
        assertThat(first.getAge()).isGreaterThanOrEqualTo(0);
        assertThat(first.getMedications()).isNotNull();
        assertThat(first.getAllergies()).isNotNull();

        assertThat(body.getResidents().size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void unknownAddress_throwsAddressNotFoundException() {
        assertThatThrownBy(() -> controller.getResidentsByAddress("Unknown Address"))
                .isInstanceOf(AddressNotFoundException.class);
    }

}
