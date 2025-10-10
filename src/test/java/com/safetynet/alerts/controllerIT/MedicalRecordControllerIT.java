package com.safetynet.alerts.controllerIT;

import com.safetynet.alerts.controller.MedicalRecordController;
import com.safetynet.alerts.exception.DuplicateMedicalRecordException;
import com.safetynet.alerts.exception.MedicalRecordNotFoundException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class MedicalRecordControllerIT {

    @Autowired
    private MedicalRecordController controller;

    @Autowired
    private DataRepository dataRepository;

    @BeforeEach
    public void reload() {
        dataRepository.reloadData();
    }

    // POST

    @Test
    public void addMedicalRecord_Successful() {

        MedicalRecord newMedicalRecord = new MedicalRecord(
                "Alice", "Dupont",
                "01/01/1997",
                List.of("vitamin C:500mg"),
                List.of("pollen")
        );

        ResponseEntity<?> response = controller.addMedicalRecord(newMedicalRecord);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isInstanceOf(MedicalRecord.class);

        MedicalRecord body = (MedicalRecord) response.getBody();
        assert body != null;
        assertThat(body.getFirstName()).isEqualTo("Alice");
        assertThat(body.getLastName()).isEqualTo("Dupont");
        assertThat(body.getBirthdate()).isEqualTo("01/01/1997");
        assertThat(body.getMedications()).containsExactly("vitamin C:500mg");
        assertThat(body.getAllergies()).containsExactly("pollen");
    }

    @Test
    public void addMedicalRecord_Duplicate_ThrowsDuplicateMedicalRecordException() {
        MedicalRecord existing = new MedicalRecord(
                "John", "Boyd",
                "03/06/1984",
                List.of("aznol:350mg", "hydrapermazol:100mg"),
                List.of("nillacilan")
        );

        assertThatThrownBy(() -> controller.addMedicalRecord(existing))
                .isInstanceOf(DuplicateMedicalRecordException.class);
    }

    // PUT

    @Test
    public void updateMedicalRecord_Successful() {
        String firstName = "John";
        String lastName = "Boyd";
        MedicalRecord payload = new MedicalRecord(
                firstName, lastName,
                "03/06/1999",
                List.of("aznol:350mg", "hydrapermazol:100mg", "alprazolam:0,25mg"),
                List.of("nillacilan", "Bet v1")
        );

        ResponseEntity<MedicalRecord> response = controller.updateMedicalRecord(firstName, lastName, payload);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();

        MedicalRecord body = response.getBody();
        assertThat(body.getFirstName()).isEqualTo("John");
        assertThat(body.getLastName()).isEqualTo("Boyd");
        assertThat(body.getBirthdate()).isEqualTo("03/06/1999");
        assertThat(body.getMedications()).containsExactly("aznol:350mg", "hydrapermazol:100mg", "alprazolam:0,25mg");
        assertThat(body.getAllergies()).containsExactly("nillacilan", "Bet v1");
    }

    @Test
    public void updateMedicalRecord_NotFound_ThrowsMedicalRecordNotFoundException() {
        String firstName = "Jane";
        String lastName = "Unknown";
        MedicalRecord payload = new MedicalRecord(
                firstName, lastName,
                "01/01/2000",
                List.of("aspirin:100mg"),
                List.of("cats")
        );

        assertThatThrownBy(() -> controller.updateMedicalRecord(firstName, lastName, payload))
                .isInstanceOf(MedicalRecordNotFoundException.class);
    }

    @Test
    public void updateMedicalRecord_PathBodyMismatch_ThrowsIllegalArgumentException() {
        String pathFirst = "John";
        String pathLast = "Boyd";
        MedicalRecord payload = new MedicalRecord(
                "John", "DifferentLastName",
                "01/01/1990",
                List.of(), List.of()
        );

        assertThatThrownBy(() -> controller.updateMedicalRecord(pathFirst, pathLast, payload))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // DELETE

    @Test
    public void deleteMedicalRecord_Successful() {
        String firstName = "John";
        String lastName = "Boyd";

        ResponseEntity<Void> response = controller.deleteMedicalRecord(firstName, lastName);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void deleteMedicalRecord_NotFound_ThrowsMedicalRecordNotFoundException() {
        String firstName = "Jane";
        String lastName = "Unknown";

        assertThatThrownBy(() -> controller.deleteMedicalRecord(firstName, lastName))
                .isInstanceOf(MedicalRecordNotFoundException.class);
    }

}
