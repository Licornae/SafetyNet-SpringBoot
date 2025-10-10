package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.EmailDTO;
import com.safetynet.alerts.exception.CityNotFoundException;
import com.safetynet.alerts.exception.GlobalExceptionHandler;
import com.safetynet.alerts.service.CommunityEmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.mockito.Mockito.when;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(CommunityEmailController.class)
public class CommunityEmailControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CommunityEmailService communityEmailService;

    @Test
    public void getCommunityEmails_ValidCity_Returns200() throws Exception {
        List<EmailDTO> culverEmails = List.of(new EmailDTO("jaboyd@email.com"), new EmailDTO("tcoop@ymail.com"), new EmailDTO("lily@email.com"));

        when(communityEmailService.getEmailsByCity("Culver")).thenReturn(culverEmails);

        mockMvc.perform(get("/communityEmail").param("city", "Culver"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].email").value("jaboyd@email.com"));
    }

    @Test
    public void getCommunityEmails_UnknownCity_Returns404WithMessage() throws Exception {
        when(communityEmailService.getEmailsByCity("Unknown"))
                .thenThrow(new CityNotFoundException("Unknown city"));

        mockMvc.perform(get("/communityEmail").param("city", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Unknown city"))
                .andExpect(jsonPath("$.path").value("/communityEmail"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void getCommunityEmails_blankCity_returns400() throws Exception {
        mockMvc.perform(get("/communityEmail").param("city", " "))
                .andExpect(status().isBadRequest());
    }

}
