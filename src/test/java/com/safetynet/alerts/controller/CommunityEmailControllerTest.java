package com.safetynet.alerts.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(CommunityEmailController.class)
public class CommunityEmailControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CommunityEmailService communityEmailService;

    @Test
    public void getCommunityEmails_ValidCity_Returns200() throws Exception {
        List<String> culverEmails = List.of("jaboyd@email.com", "tcoop@ymail.com", "lily@email.com");

        when(communityEmailService.getEmailsByCity("Culver")).thenReturn(culverEmails);

        mockMvc.perform(get("/communityEmail").param("city", "Culver"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*]", hasItem("tcoop@ymail.com")));
    }

    @Test
    public void getCommunityEmails_UnknownCity_Returns404WithMessage() throws Exception {
        when(communityEmailService.getEmailsByCity("Unknown"))
                .thenThrow(new CityNotFoundException("Unknown city"));

        mockMvc.perform(get("/communityEmail").param("city", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Unknown city"));
    }

    @Test
    public void getCommunityEmails_blankCity_returns400() throws Exception {
        mockMvc.perform(get("/communityEmail").param("city", " "))
                .andExpect(status().isBadRequest());
    }

}
