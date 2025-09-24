package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.FloodHouseholdDTO;
import com.safetynet.alerts.service.FloodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.safetynet.alerts.dto.PersonMedicalInfoDTO;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(FloodController.class)
public class FloodControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    FloodService floodService;

    @Test
    void getHouseholdsByStations_ok() throws Exception {
        List<PersonMedicalInfoDTO> residentsAt1509CulverSt = List.of(
                new PersonMedicalInfoDTO("John","Boyd","841-874-6512",41,
                        List.of("aznol:350mg","hydrapermazol:100mg"),
                        List.of("nillacilan")),
                new PersonMedicalInfoDTO("Jacob","Boyd","841-874-6513",36,
                        List.of("pharmacol:5000mg","terazine:10mg"),
                        List.of())
        );
        List<FloodHouseholdDTO> floodHouseholdDTO = List.of(
                new FloodHouseholdDTO("1509 Culver St", residentsAt1509CulverSt)
        );

        when(floodService.getHouseholdsByStations(List.of("1","3"))).thenReturn(floodHouseholdDTO);

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1,3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address").value("1509 Culver St"))
                .andExpect(jsonPath("$[0].residents[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].residents[0].medications[1]").value("hydrapermazol:100mg"));
    }

    @Test
    void getHouseholdsByStations_blank_returns400() throws Exception {
        mockMvc.perform(get("/flood/stations").param("stations", " "))
                .andExpect(status().isBadRequest());
    }
}
