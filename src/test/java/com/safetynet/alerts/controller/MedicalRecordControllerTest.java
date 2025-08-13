package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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

    //GET

    @Test
    public void testGetMedicalRecordByFirstAndLastName_ReturnsAllFields() throws Exception {
        // Arrange
        MedicalRecord mockRecord = new MedicalRecord(
                "John", "Boyd", "03/06/1984",
                List.of("aznol:350mg", "hydrapermazol:100mg"),
                List.of("nillacilan")
        );

        when(medicalRecordService.getMedicalRecord("John", "Boyd")).thenReturn(mockRecord);

        // Act & Assert
        mockMvc.perform(get("/medicalRecord")
                        .param("firstName", "John")
                        .param("lastName", "Boyd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Boyd"))
                .andExpect(jsonPath("$.birthdate").value("03/06/1984"))
                .andExpect(jsonPath("$.medications[0]").value("aznol:350mg"))
                .andExpect(jsonPath("$.medications[1]").value("hydrapermazol:100mg"))
                .andExpect(jsonPath("$.allergies[0]").value("nillacilan"));
    }

    //POST

    @Test
    public void testAddANewMedicalRecord_Successful() throws Exception {
        // Arrange
        MedicalRecord newMedicalRecord = new MedicalRecord(
                "John", "Boyd", "03/06/1984",
                List.of("aznol:350mg", "hydrapermazol:100mg"),
                List.of("nillacilan")
        );

        when(medicalRecordService.addMedicalRecord(any(MedicalRecord.class)).thenReturn(newMedicalRecord));

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
    void testAddMedicalRecord_ExistingMedicalRecord_ReturnsConflict() throws Exception {
        // Arrange
        MedicalRecord existing = new MedicalRecord(
                "John", "Boyd", "03/06/1984",
                List.of("aznol:350mg", "hydrapermazol:100mg"),
                List.of("nillacilan")
        );

        when(medicalRecordService.getMedicalRecord(existing.getFirstName(), existing.getLastName())).thenReturn(existing);

        // Act & Assert
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existing))
                )
                .andExpect(status().isConflict())
                .andExpect(content().string("This person already have a medical record"));
    }

    @Test
    void testAddMedicalRecord_MissingBirthday_ReturnsBadRequest() throws Exception {
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
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Birthdate required")));
    }
    }
