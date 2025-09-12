package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.dto.FirestationCoverageDTO;
import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.service.FireStationQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(FireStationQueryController.class)
public class FireStationQueryControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    FireStationQueryService queryService;

    @Test
    public void testGetCoverageByStation_ReturnsPersonsAndCounts() throws Exception {
        var persons = List.of(
                new PersonDTO("John","Boyd",41,"1509 Culver St","841-874-6512"),
                new PersonDTO("Jacob","Boyd",36,"1509 Culver St","841-874-6513")
        );
        var dto = new FirestationCoverageDTO(1,persons, 2, 1);
        when(queryService.getCoverageByStation("1")).thenReturn(dto);

        mockMvc.perform(get("/firestation")
                        .param("stationNumber","1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.persons[0].firstName").value("John"))
                .andExpect(jsonPath("$.persons[1].phone").value("841-874-6513"))
                .andExpect(jsonPath("$.adults").value(2))
                .andExpect(jsonPath("$.children").value(1));
    }
}

