package com.safetynet.alerts.controllerIT;

import com.safetynet.alerts.controller.FloodController;
import com.safetynet.alerts.dto.FloodHouseholdDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class FloodControllerTestIT {

    @Autowired
    private FloodController controller;

    @Autowired
    private DataRepository dataRepository;

    @BeforeEach
    public void reload() {
        dataRepository.reloadData();
    }

    @Test
    public void existingStations_ReturnsHouseholdsGroupedByAddress() {
        String stations = "1,2";

        ResponseEntity<List<FloodHouseholdDTO>> response = controller.getHouseholdsByStations(stations);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();

        FloodHouseholdDTO first = Objects.requireNonNull(response.getBody()).get(0);
        assertThat(first.getStation()).isNotBlank();
        assertThat(first.getAddress()).isNotBlank();
        assertThat(first.getResidents()).isNotNull();

        assertThat(first.getResidents().get(0).getFirstName()).isNotBlank();
        assertThat(first.getResidents().get(0).getLastName()).isNotBlank();
        assertThat(first.getResidents().get(0).getPhone()).isNotBlank();
        assertThat(first.getResidents().get(0).getAge()).isGreaterThanOrEqualTo(0);
        assertThat(first.getResidents().get(0).getMedications()).isNotNull();
        assertThat(first.getResidents().get(0).getAllergies()).isNotNull();
    }

    @Test
    public void unknownStations_ThrowsAddressNotFoundException() {
        assertThatThrownBy(() -> controller.getHouseholdsByStations("999,998"))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    public void onlyCommas_ThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> controller.getHouseholdsByStations(", ,"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stations must not be blank");
    }
}
