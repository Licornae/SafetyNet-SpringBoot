package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.ChildAlertDTO;
import com.safetynet.alerts.dto.FamilyMembersDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.service.ChildAlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import static org.mockito.Mockito.when;
@WebMvcTest(ChildAlertController.class)
public class ChildAlertControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ChildAlertService childAlertService;

    @Test
    public void testGetChildAlertByAddress_ReturnsChildrenListAndFamilyMembers() throws Exception {

        // Arrange
        var familyMembers = List.of(new FamilyMembersDTO("John","Boyd","Adult"),
                new FamilyMembersDTO("Jacob","Boyd","Adult"),
                new FamilyMembersDTO("Tenley","Boyd","Child"),
                new FamilyMembersDTO("Roger","Boyd","Child"),
                new FamilyMembersDTO("Felicia","Boyd","Adult")
        );

        var childAlertDTO = List.of(new ChildAlertDTO("Tenley","Boyd",13, familyMembers),
                new ChildAlertDTO("Roger","Boyd",8, familyMembers) );


        when(childAlertService.getChildrenByAddress("1509 Culver St"))
            .thenReturn(childAlertDTO);

        // Act & Assert
        mockMvc.perform(get("/childAlert")
                        .param("address","1509 Culver St")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Tenley"))
                .andExpect(jsonPath("$[0].lastName").value("Boyd"))
                .andExpect(jsonPath("$[0].age").value(13))
                .andExpect(jsonPath("$[0].familyMembers[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].familyMembers[0].lastName").value("Boyd"))
                .andExpect(jsonPath("$[0].familyMembers[0].ageCategory").value("Adult"))
                .andExpect(jsonPath("$[0].familyMembers[1].firstName").value("Jacob"))
                .andExpect(jsonPath("$[0].familyMembers[2].lastName").value("Boyd"))
                .andExpect(jsonPath("$[0].familyMembers[3].ageCategory").value("Child"))
                .andExpect(jsonPath("$[0].familyMembers[4].firstName").value("Felicia"))
                .andExpect(jsonPath("$[1].firstName").value("Roger"))
                .andExpect(jsonPath("$[1].familyMembers[0].firstName").value("John"));
 }

    @Test
    public void existing_address_with_only_adults_returns_empty_array() throws Exception {
        when(childAlertService.getChildrenByAddress("644 Gershwin Cir"))
                .thenReturn(List.of());

        mockMvc.perform(get("/childAlert").param("address","644 Gershwin Cir"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void unknown_Address_Returns404() throws Exception {
        when(childAlertService.getChildrenByAddress("Unknown"))
                .thenThrow(new AddressNotFoundException("Address not found"));

        mockMvc.perform(get("/childAlert").param("address","Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Address not found"))
                .andExpect(jsonPath("$.path").value("/childAlert"));
    }

}
