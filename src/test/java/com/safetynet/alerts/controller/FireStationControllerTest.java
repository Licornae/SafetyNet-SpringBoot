package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.exception.PersonNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.FireStationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(FireStationController.class)
public class FireStationControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FireStationService fireStationService;

    @Test
    void getStationNumberByAddress_ReturnsStationNumber() throws Exception {
        // Arrange
        String address = "1509 Culver St";
        FireStation mockStation = new FireStation(address, "3");
        when(fireStationService.getFireStation(address)).thenReturn(mockStation);

        // Act & Assert
        mockMvc.perform(get("/firestation").param("address", address))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.station").value("3"))
                .andExpect(jsonPath("$.address").value(address));
    }

    @Test
    public void whenAddFireStation_Successful() throws Exception {
        // Arrange
        FireStation newfireStation = new FireStation("1509 Culver St", "3");

        when(fireStationService.addFireStation(any(FireStation.class))).thenReturn(newfireStation);

        // Act & Assert
        mockMvc.perform(post("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newfireStation))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value("1509 Culver St"))
                .andExpect(jsonPath("$.station").value("3"));
    }

    @Test
    void addFireStation_Duplicate_ReturnsConflict() throws Exception {
        // Arrange
        FireStation duplicate = new FireStation("1509 Culver St", "3");

        when(fireStationService.getFireStation("1509 Culver St")).thenReturn(duplicate);

        // Act & Assert
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Cette adresse renseigne déjà une station"));
    }

    @Test
    public void testUpdateStationAddress_Successful() throws Exception {
        // Arrange
        String address = "1509 Culver St";
        String updatedPayload = "{\"address\":\"1509 Culver St\",\"station\":\"2\"}";

        when(fireStationService.updateStationAddress(eq(address), any(FireStation.class)))
                .thenReturn(new FireStation(address, "2"));

        // Act & Assert
        mockMvc.perform(put("/firestation/{address}", "1509 Culver St")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("1509 Culver St"))
                .andExpect(jsonPath("$.station").value("2"));
    }

    @Test
    public void testUpdateStationAddress_NotFound() throws Exception {
        // Arrange
        String address = "Unknown";
        String updatedPayload = "{\"address\":\"Unknown\",\"station\":\"2\"}";

        when(fireStationService.updateStationAddress(eq(address), any(FireStation.class)))
                .thenThrow(new AdressNotFoundException());

        // Act & Assert
        mockMvc.perform(put("/firestation/{address}", "Unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPayload))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateStationAddress_BadRequest() throws Exception {
        // Arrange
        String address = "1509 Culver St";
        String badPayload = "{\"address\":\"1509 Culver St\"}";

        // Act & Assert
        mockMvc.perform(put("/person/{address}", "1509 Culver St")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badPayload))
                .andExpect(status().isBadRequest());
    }

}

