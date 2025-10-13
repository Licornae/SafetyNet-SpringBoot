package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.exception.DuplicateFireStationException;
import com.safetynet.alerts.model.FireStation;

import com.safetynet.alerts.service.FireStationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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
    public void testAddFireStation_ExistingFireStation_Returns409() throws Exception {
        FireStation payload = new FireStation("1509 Culver St", "3");

        when(fireStationService.addFireStation(any(FireStation.class)))
                .thenThrow(new DuplicateFireStationException("This address already refers to a station: 1509 Culver St"));

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(containsString("This address already refers to a station")))
                .andExpect(jsonPath("$.path").value("/firestation"))
                .andExpect(jsonPath("$.timestamp").exists());
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
    public void testUpdateStationAddress_NotFound_Returns404() throws Exception {
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
    public void testUpdateStationAddress_PathBodyMismatch_Returns400() throws Exception {
        String pathAddress = "1509 Culver St";
        FireStation body = new FireStation("MISMATCH", "3");

        mockMvc.perform(put("/firestation/{address}", pathAddress)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Path/body mismatch")))
                .andExpect(jsonPath("$.path").value("/firestation/1509%20Culver%20St"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testUpdateStationAddress_BadRequest_Returns400() throws Exception {
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
    public void testDeleteFireStation_Successful() throws Exception {
        String address = "1509 Culver St";
        when(fireStationService.deleteFireStationByAddress(address)).thenReturn(true);

        mockMvc.perform(delete("/firestation").param("address", address))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteFireStation_AddressNotFound_Returns404() throws Exception {
        String address = "unknown";

        doThrow(new AddressNotFoundException("Address not found : " + address))
                .when(fireStationService).deleteFireStationByAddress(address);

        mockMvc.perform(delete("/firestation").param("address", address))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Address not found")))
                .andExpect(jsonPath("$.path", containsString("/firestation")));
    }

    @Test
    public void deleteByStation_ok_returns204() throws Exception {
        //confirm=YES, the service execute a deletion
        when(fireStationService.countByStation(eq("3"))).thenReturn(5);
        when(fireStationService.deleteFireStationsByStation(eq("3"))).thenReturn(true);

        mockMvc.perform(delete("/firestation")
                        .param("station", "3")
                        .param("confirm", "YES"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteByStation_missingConfirm_returns400() throws Exception {
        //no confirmation => 400
        when(fireStationService.countByStation(eq("3"))).thenReturn(5);

        mockMvc.perform(delete("/firestation")
                        .param("station", "3"))
                .andExpect(status().isBadRequest());
    }

}

