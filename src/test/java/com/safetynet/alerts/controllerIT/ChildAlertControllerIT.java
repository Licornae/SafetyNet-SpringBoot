package com.safetynet.alerts.controllerIT;

import com.safetynet.alerts.controller.ChildAlertController;
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
public class ChildAlertControllerIT {

    @Autowired
    private ChildAlertController controller;

    @Autowired
    private DataRepository dataRepository;

    @BeforeEach
    public void reload() {
        dataRepository.reloadData();
    }

    @Test
    public void existingAddress_ReturnsChildrenAndFamily() {
        ResponseEntity<?> response = controller.getChildAlertByAddress("1509 Culver St");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();

    }

    @Test
    public void unknownAddress_ThrowsAddressNotFoundException() {
        assertThatThrownBy(() -> controller.getChildAlertByAddress("Unknown Address"))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    public void adultsOnlyAddress_ReturnsEmptyList() {
        ResponseEntity<?> response = controller.getChildAlertByAddress("644 Gershwin Cir");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
    }
}
