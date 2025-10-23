package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.PersonInfoDTO;
import com.safetynet.alerts.exception.PersonNotFoundException;
import com.safetynet.alerts.service.PersonInfoService;
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

@WebMvcTest(PersonInfoController.class)
public class PersonInfoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PersonInfoService personInfoService;

    @Test
    public void getPersonInfo_ValidParameter_Returns200WithList() throws Exception {

        List<PersonInfoDTO> boyds = List.of(
                new PersonInfoDTO("John",   "Boyd", "1509 Culver St", 40, "john.boyd@email.com", List.of("aznol:200mg"), List.of("nillacilan")),
                new PersonInfoDTO("Jacob",  "Boyd", "1509 Culver St", 36, "jacob.boyd@email.com", List.of("pharmacol:5000mg"), List.of()),
                new PersonInfoDTO("Tenley", "Boyd", "1509 Culver St", 12, "tenley.boyd@email.com", List.of("ibupurin:200mg"), List.of("peanut"))
        );

        when(personInfoService.getPersonInfoByLastName("Boyd")).thenReturn(boyds);

        mockMvc.perform(get("/personInfolastName").param("lastName", "Boyd"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].lastName", everyItem(equalToIgnoringCase("Boyd"))))
                .andExpect(jsonPath("$[?(@.firstName=='John')].email", hasItem("john.boyd@email.com")))
                .andExpect(jsonPath("$[?(@.firstName=='Jacob')].medications[0]", hasItem("pharmacol:5000mg")))
                .andExpect(jsonPath("$[?(@.firstName=='Tenley')].allergies[0]", hasItem("peanut")));

    }

    @Test
    public void getPersonInfo_UnknownLastName_Returns404NotFound() throws Exception {
        when(personInfoService.getPersonInfoByLastName("Unknown")).thenThrow(new PersonNotFoundException("Person not found"));

        mockMvc.perform(get("/personInfolastName").param("lastName", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Person not found"))
                .andExpect(jsonPath("$.path").value("/personInfolastName"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
