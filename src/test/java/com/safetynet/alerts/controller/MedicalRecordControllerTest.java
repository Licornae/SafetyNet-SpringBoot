package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(MedicalRecordController.class)
public class MedicalRecordControllerTest {

        @Autowired
        public MockMvc mockMvc;

        @MockitoBean
        private MedicalRecordService medicalRecordService;

        @Autowired
        private ObjectMapper objectMapper;

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
    }
