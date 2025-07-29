package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PersonService personService;

    @Test
    public void whenGetPersonByFirstAndLastName_thenReturnAllFields() throws Exception {
        // Simule une Person pour ce test
        Person mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("Boyd");
        mockPerson.setAddress("1509 Culver St");
        mockPerson.setCity("Culver");
        mockPerson.setZip("97451");
        mockPerson.setPhone("841-874-6512");
        mockPerson.setEmail("jaboyd@email.com");

        when(personService.getPerson("John", "Boyd")).thenReturn(mockPerson);

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

    @Test
    void addANewPerson() throws Exception {
        // Arrange
        Person newPerson = new Person("Jane","Doe","56 River St", "Culver", "97451", "841-874-7650", "janedoe@email.com");

        when(personService.addPerson(
                org.mockito.ArgumentMatchers.any(Person.class))
        ).thenReturn(newPerson);

        // Act & Assert
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerson))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.address").value("56 River St"))
                .andExpect(jsonPath("$.city").value("Culver"))
                .andExpect(jsonPath("$.zip").value("97451"))
                .andExpect(jsonPath("$.phone").value("841-874-7650"))
                .andExpect(jsonPath("$.email").value("janedoe@email.com"));
    }

    @Test
    void addPerson_ExistingPerson_ReturnsConflict() throws Exception {
        // Arrange
        Person existing = new Person("Jane","Doe","56 River St", "Culver", "97451", "841-874-7650", "janedoe@email.com");

        // Simule que la personne existe déjà
        when(personService.getPerson(existing.getFirstName(), existing.getLastName())).thenReturn(existing);

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existing))
                )
                .andExpect(status().isConflict())
                .andExpect(content().string("Cette personne existe déjà"));
    }

    @Test
    void addPerson_MissingFirstName_ReturnsBadRequest() throws Exception {
        // Arrange
        Person invalidPerson = new Person("", "Doe", "56 River St", "Culver", "97451", "841-874-7650", "janedoe@email.com");

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPerson))
                )
                .andExpect(status().isBadRequest());
    }

}

