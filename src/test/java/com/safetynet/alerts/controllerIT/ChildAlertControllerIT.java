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
public class ChildAlertControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void childAlert_ExistingAddressWithChildren_Returns200() throws Exception {
        mockMvc.perform(get("/childAlert")
                        .param("address", "1509 Culver St")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].age", everyItem(lessThanOrEqualTo(18))))
                .andExpect(jsonPath("$[*].firstName", hasItem("Tenley")))
                .andExpect(jsonPath("$[*].lastName", hasItem("Boyd")))
                .andExpect(jsonPath("$[*].familyMembers").isArray());
    }

    @Test
    public void childAlert_ExistingAddressOnlyAdults_Returns200_AndEmptyArray() throws Exception {
        mockMvc.perform(get("/childAlert")
                        .param("address", "644 Gershwin Cir")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(""));
    }

    @Test
    public void childAlert_UnknownAddress_Returns404() throws Exception {
        mockMvc.perform(get("/childAlert")
                        .param("address", "Unknown Address")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Address not found"))
                .andExpect(jsonPath("$.path").value("/childAlert"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void childAlert_BlankAddress_Returns400() throws Exception {
        mockMvc.perform(get("/childAlert")
                        .param("address", " ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
