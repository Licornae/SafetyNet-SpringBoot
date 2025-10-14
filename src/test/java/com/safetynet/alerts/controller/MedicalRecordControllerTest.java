package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.exception.DuplicateMedicalRecordException;
import com.safetynet.alerts.exception.MedicalRecordNotFoundException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicalRecordController.class)
public class MedicalRecordControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @MockitoBean
    private MedicalRecordService medicalRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    //POST

    @Test
    public void testAddANewMedicalRecord_Successful() throws Exception {
        // Arrange
        MedicalRecord newMedicalRecord = new MedicalRecord(
                "John", "Boyd", "03/06/1984",
                List.of("aznol:350mg", "hydrapermazol:100mg"),
                List.of("nillacilan")
        );

        when(medicalRecordService.addMedicalRecord(any(MedicalRecord.class))).thenReturn(newMedicalRecord);

        // Act & Assert
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalRecord)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Boyd"))
                .andExpect(jsonPath("$.birthdate").value("03/06/1984"))
                .andExpect(jsonPath("$.medications[0]").value("aznol:350mg"))
                .andExpect(jsonPath("$.medications[1]").value("hydrapermazol:100mg"))
                .andExpect(jsonPath("$.allergies[0]").value("nillacilan"));
    }

    @Test
    public void addMedicalRecord_WhenDuplicate_Throws409() throws Exception {
        // Arrange
        MedicalRecord existing = new MedicalRecord(
                "John", "Boyd", "03/06/1984",
                List.of("aznol:350mg", "hydrapermazol:100mg"),
                List.of("nillacilan")
        );

        when(medicalRecordService.addMedicalRecord(any(MedicalRecord.class)))
                .thenThrow(new DuplicateMedicalRecordException("Medical record already exists for: John Boyd"));

        // Act & Assert
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existing)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(containsString("already exists")))
                .andExpect(jsonPath("$.path").value("/medicalRecord"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testAddMedicalRecord_MissingBirthday_ReturnsBadRequest() throws Exception {
        // Arrange
        MedicalRecord invalidRecord = new MedicalRecord(
                "John", "Boyd", "",
                List.of("aznol:350mg", "hydrapermazol:100mg"),
                List.of("nillacilan")
        );

        // Act & Assert
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRecord))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Birthdate required")));
    }

    //PUT
    @Test
    public void testUpdateMedicalRecord_Successful() throws Exception {
        // Arrange
        String firstName = "John";
        String lastName = "Boyd";
        String updatedPayload = "{\"firstName\":\"John\", \"lastName\":\"Boyd\", \"birthdate\":\"03/06/1999\", \"medications\":[\"aznol:350mg\", \"hydrapermazol:100mg\", \"alprazolam:0,25mg\"], \"allergies\":[\"nillacilan\",\"Bet v1\"]}";

        when(medicalRecordService.updateMedicalRecord(eq(firstName), eq(lastName), any(MedicalRecord.class)))
                .thenReturn(new MedicalRecord(firstName,lastName,"03/06/1999", List.of("aznol:350mg", "hydrapermazol:100mg", "alprazolam:0,25mg"), List.of("nillacilan","Bet v1")));

        // Act & Assert
        mockMvc.perform(put("/medicalRecord/{firstName}/{lastName}", "John", "Boyd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Boyd"))
                .andExpect(jsonPath("$.birthdate").value("03/06/1999"))
                .andExpect(jsonPath("$.medications[0]").value("aznol:350mg"))
                .andExpect(jsonPath("$.medications[1]").value("hydrapermazol:100mg"))
                .andExpect(jsonPath("$.medications[2]").value("alprazolam:0,25mg"))
                .andExpect(jsonPath("$.allergies[0]").value("nillacilan"))
                .andExpect(jsonPath("$.allergies[1]").value("Bet v1"));
    }

    @Test
    public void testUpdateMedicalRecord_NotFound() throws Exception {
        // Arrange
        String firstName = "John";
        String lastName = "Unknown";
        String updatedPayload = "{\"firstName\":\"John\", \"lastName\":\"Unknown\", \"birthdate\":\"03/06/1999\", \"medications\":[\"aznol:350mg\", \"hydrapermazol:100mg\", \"alprazolam:0,25mg\"], \"allergies\":[\"nillacilan\",\"Bet v1\"]}";

        when(medicalRecordService.updateMedicalRecord(eq(firstName), eq(lastName), any(MedicalRecord.class)))
                .thenThrow(new MedicalRecordNotFoundException());

        // Act & Assert
        mockMvc.perform(put("/medicalRecord/{firstName}/{lastName}", "John", "Unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPayload))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateMedicalRecord_BadRequest() throws Exception {
        // Arrange
        String firstName = "John";
        String lastName = "Boyd";
        // Payload invalide (champ manquant)
        String badPayload = "{\"firstName\":\"John\",\"lastName\":\"Boyd\"}";

        // Act & Assert
        mockMvc.perform(put("/medicalRecord/{firstName}/{lastName}", "John", "Boyd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateMedicalRecord_PathBodyMismatch_ReturnsBadRequest() throws Exception {
        String pathFirst = "John";
        String pathLast = "Boyd";

        String mismatchedPayload = """
        {
          "firstName": "John",
          "lastName": "Different",
          "birthdate": "01/01/1990",
          "medications": [],
          "allergies": []
        }
        """;

        mockMvc.perform(put("/medicalRecord/{firstName}/{lastName}", pathFirst, pathLast)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mismatchedPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Path {firstName,lastName} must match body."))
                .andExpect(jsonPath("$.path").value("/medicalRecord/John/Boyd"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // DELETE
    @Test
    public void testDeleteMedicalRecord_Successful() throws Exception {
        // Arrange
        String firstName = "John";
        String lastName = "Boyd";
        when(medicalRecordService.deleteMedicalRecord(firstName, lastName)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/medicalRecord/{firstName}/{lastName}", firstName, lastName))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteMedicalRecord_NotFound_Returns404_NoBody() throws Exception {
        String firstName = "Jane";
        String lastName = "Unknown";
        when(medicalRecordService.deleteMedicalRecord(firstName, lastName)).thenReturn(false);

        mockMvc.perform(delete("/medicalRecord/{firstName}/{lastName}", firstName, lastName))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}
