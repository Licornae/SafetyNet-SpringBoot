package com.safetynet.alerts.controller;


import com.safetynet.alerts.exception.StationNotFoundException;
import com.safetynet.alerts.service.PhoneAlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(PhoneAlertController.class)
public class PhoneAlertControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PhoneAlertService phoneAlertService;

    @Test
    public void phoneAlert_returns_unique_sorted_numbers_for_station() throws Exception {
        String station = "2";
        List<String> phones = List.of("841-874-6512", "841-874-6513", "841-874-7458");
        when(phoneAlertService.getPhonesByFirestation(station)).thenReturn(phones);

        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", station)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("841-874-6512"))
                .andExpect(jsonPath("$[1]").value("841-874-6513"))
                .andExpect(jsonPath("$[2]").value("841-874-7458"));
    }

    @Test
    public void unknown_station_returns_404_with_message() throws Exception {
        when(phoneAlertService.getPhonesByFirestation("999"))
                .thenThrow(new StationNotFoundException("This station doesn't exist"));

        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", "999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("This station doesn't exist"))
                .andExpect(jsonPath("$.path").value("/phoneAlert"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void blank_station_returns_bad_request() throws Exception {
        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", " ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
