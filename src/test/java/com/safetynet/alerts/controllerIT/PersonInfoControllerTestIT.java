package com.safetynet.alerts.controllerIT;

import com.safetynet.alerts.controller.PersonInfoController;
import com.safetynet.alerts.dto.PersonInfoDTO;
import com.safetynet.alerts.exception.PersonNotFoundException;
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
public class PersonInfoControllerTestIT {

    @Autowired
    private PersonInfoController controller;

    @Autowired
    private DataRepository dataRepository;

    @BeforeEach
    public void reload() {
        dataRepository.reloadData();
    }

    @Test
    public void existingLastName_ReturnsPersonsWithAllInfo() {

        String lastName = "Boyd";

        ResponseEntity<List<PersonInfoDTO>> response = controller.getPersonInfoByLastName(lastName);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull().isNotEmpty();

        PersonInfoDTO first = response.getBody().get(0);
        assertThat(first.getFirstName()).isNotBlank();
        assertThat(first.getLastName()).isEqualTo(lastName);
        assertThat(first.getAddress()).isNotBlank();
        assertThat(first.getEmail()).isNotBlank();
        assertThat(first.getAge()).isNotNull().isGreaterThanOrEqualTo(0);
        assertThat(first.getMedications()).isNotNull();
        assertThat(first.getAllergies()).isNotNull();
    }

    @Test
    public void unknownLastName_ThrowsPersonNotFoundException() {
        assertThatThrownBy(() -> controller.getPersonInfoByLastName("Unknown"))
                .isInstanceOf(PersonNotFoundException.class);
    }

}
