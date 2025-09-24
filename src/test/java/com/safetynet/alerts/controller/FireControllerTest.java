package com.safetynet.alerts.controller;


import com.safetynet.alerts.dto.FireDTO;
import com.safetynet.alerts.dto.PersonMedicalInfoDTO;
import com.safetynet.alerts.service.FireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(FireController.class)
public class FireControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    FireService fireService;

    @Test
    public void testGetResidentsInfosByAddress_ReturnsListOfResidentsWithInfosAndStation()  throws Exception {

        List<PersonMedicalInfoDTO> residents = List.of(
                new PersonMedicalInfoDTO("John","Boyd","841-874-6512",41, List.of("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan")),
                new PersonMedicalInfoDTO("Jacob", "Boyd", "841-874-6513",36,List.of("pharmacol:5000mg", "terazine:10mg"), List.of())
        );

        FireDTO fireDTO = new FireDTO("3", residents);

        when(fireService.getResidentsByAddress("1509 Culver St")).thenReturn(fireDTO);

        mockMvc.perform(get("/fire")
                        .param("address", "1509 Culver St")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.station").value("3"))
                .andExpect(jsonPath("$.residents[0].firstName").value("John"))
                .andExpect(jsonPath("$.residents[0].age").value(41))
                .andExpect(jsonPath("$.residents[1].firstName").value("Jacob"))
                .andExpect(jsonPath("$.residents[0].medications[0]").value("aznol:350mg"))
                .andExpect(jsonPath("$.residents[0].medications[1]").value("hydrapermazol:100mg"))
                .andExpect(jsonPath("$.residents[0].allergies[0]").value("nillacilan"))
                .andExpect(jsonPath("$.residents[1].allergies").isEmpty());
    }
    @Test
    public void blank_address_returns400() throws Exception {
        mockMvc.perform(get("/fire").param("address", " "))
                .andExpect(status().isBadRequest());
    }
}
