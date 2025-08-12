package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.model.FireStation;

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

    //GET

    @Test
    void testGetStationNumberByAddress_ReturnsStationNumber() throws Exception {
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

    //POST

    @Test
    public void testAddFireStation_Successful() throws Exception {
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
    void testAddFireStation_ExistingFireStation_ReturnsConflict() throws Exception {
        // Arrange
        FireStation duplicate = new FireStation("1509 Culver St", "3");

        when(fireStationService.getFireStation("1509 Culver St")).thenReturn(duplicate);

        // Act & Assert
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(content().string("This address already refers to a station"));
    }

    //PUT

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
                .thenThrow(new AddressNotFoundException());

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
        mockMvc.perform(put("/firestation/{address}", "1509 Culver St")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badPayload))
                .andExpect(status().isBadRequest());
    }

    //DELETE

    @Test
    void testDeleteFireStation_Successful() throws Exception {
        String address = "1509 Culver St";
        when(fireStationService.deleteFireStationByAddress(address)).thenReturn(true);

        mockMvc.perform(delete("/firestation").param("address", address))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteFireStation_NotFound() throws Exception {
        String address = "unknown";
        when(fireStationService.deleteFireStationByAddress(address)).thenReturn(false);

        mockMvc.perform(delete("/firestation").param("address", address))
                .andExpect(status().isNotFound());
    }





}

