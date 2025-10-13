package com.safetynet.alerts.controllerIT;

import com.safetynet.alerts.controller.FireStationController;
import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.exception.DuplicateFireStationException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.DataRepository;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
public class FireStationControllerIT {

    @Autowired
    private FireStationController controller;

    @Autowired
    private DataRepository dataRepository;

    @BeforeEach
    public void reload() {
        dataRepository.reloadData();
    }

    @Test
    public void addFireStation_Successful() {
        FireStation payload = new FireStation("1909 Culver St", "2");

        ResponseEntity<?> response = controller.addFireStation(payload);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isInstanceOf(FireStation.class);

        FireStation body = (FireStation) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getAddress()).isEqualTo("1909 Culver St");
        assertThat(body.getStation()).isEqualTo("2");
    }

    @Test
    public void addFireStation_Duplicate_ThrowsDuplicateFireStationException() {

        FireStation existing = new FireStation("1509 Culver St", "3");

        assertThatThrownBy(() -> controller.addFireStation(existing))
                .isInstanceOf(DuplicateFireStationException.class);
    }

    @Test
    public void updateStationAddress_Successful() {
        String address = "1509 Culver St";
        FireStation payload = new FireStation(address, "2");

        ResponseEntity<FireStation> response = controller.updateStationAddress(address, payload);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        FireStation body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getAddress()).isEqualTo(address);
        assertThat(body.getStation()).isEqualTo("2");
    }

    @Test
    public void updateStationAddress_NotFound_ThrowsAddressNotFoundException() {
        String address = "Unknown Address";
        FireStation payload = new FireStation(address, "9");

        assertThatThrownBy(() -> controller.updateStationAddress(address, payload))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    public void updateStationAddress_PathBodyMismatch_ThrowsIllegalArgumentException() {
        String pathAddress = "1509 Culver St";
        FireStation body = new FireStation("MISMATCH", "3");

        assertThatThrownBy(() -> controller.updateStationAddress(pathAddress, body))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void deleteFireStationByAddress_Successful() {
        String address = "1509 Culver St";

        ResponseEntity<Void> response = controller.deleteFireStationByAddress(address);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void deleteFireStationByAddress_NotFound_ThrowsException() {
        String address = "Unknown Address";
        assertThatThrownBy(() -> controller.deleteFireStationByAddress(address))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    public void deleteByStation_dryRun_Returns200WithCount() {
        ResponseEntity<?> response = controller.deleteFireStationsByStation("3", true, null);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("station")).isEqualTo("3");
        assertThat((Integer) body.get("count")).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void deleteByStation_MissingConfirm_ThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> controller.deleteFireStationsByStation("3", false, String.valueOf(false)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void deleteByStation_ConfirmYES_Returns204() {
        ResponseEntity<?> response = controller.deleteFireStationsByStation("3", false, "YES");

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(response.getBody()).isNull();
    }

}
