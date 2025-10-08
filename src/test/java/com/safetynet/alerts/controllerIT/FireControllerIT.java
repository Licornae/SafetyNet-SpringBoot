package com.safetynet.alerts.controllerIT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FireControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getFire_ExistingAddress_returns200() throws Exception {
        mockMvc.perform(get("/fire")
                        .param("address", "1509 Culver St")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.station").value("3"))
                .andExpect(jsonPath("$.residents.length()", greaterThan(0)))
                .andExpect(jsonPath("$.residents[*].firstName", hasItem("John")))
                .andExpect(jsonPath("$.residents[*].medications").isArray());
    }

    @Test
    public void getFire_UnknownAddress_Returns404_WithErrorBody() throws Exception {
        mockMvc.perform(get("/fire")
                        .param("address", "Unknown Address")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", not(emptyOrNullString())))
                .andExpect(jsonPath("$.path").value("/fire"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void getfire_BlankAddress_Returns400() throws Exception {
        mockMvc.perform(get("/fire")
                        .param("address", " ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
