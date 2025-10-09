package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.dto.FirestationCoverageDTO;
import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.exception.StationNotFoundException;
import com.safetynet.alerts.service.FireStationQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    public void testGetCoverageByStation_BlankStation_Returns400() throws Exception {
        mockMvc.perform(get("/firestation")
                        .param("stationNumber"," ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid request parameters"));
    }

    @Test
    public void testGetCoverageByStation_StationNotFound_Returns404() throws Exception {
        when(queryService.getCoverageByStation("999"))
                .thenThrow(new StationNotFoundException("Station 999 not found"));

        mockMvc.perform(get("/firestation")
                        .param("stationNumber","999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Station 999 not found"))
                .andExpect(jsonPath("$.path").value("/firestation"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testGetCoverageByStation_AddressNotFound_Returns404() throws Exception {
        when(queryService.getCoverageByStation("7"))
                .thenThrow(new AddressNotFoundException("No address found for station 7"));

        mockMvc.perform(get("/firestation")
                        .param("stationNumber","7")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No address found for station 7"))
                .andExpect(jsonPath("$.path").value("/firestation"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

}

