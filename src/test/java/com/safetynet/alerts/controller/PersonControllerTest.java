package com.safetynet.alerts.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @Test
    public void whenGetPersonByFirstAndLastName_thenReturnAllFields() throws Exception {
        mockMvc.perform(get("/person")
                        .param("firstName", "John")
                        .param("lastName", "Boyd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Boyd"))
                .andExpect(jsonPath("$.address").value("1509 Culver St"))
                .andExpect(jsonPath("$.city").value("Culver"))
                .andExpect(jsonPath("$.zip").value("97451"))
                .andExpect(jsonPath("$.phone").value("841-874-6512"))
                .andExpect(jsonPath("$.email").value("jaboyd@email.com"));
    }
}

