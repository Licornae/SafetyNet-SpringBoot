package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
                any(Person.class))
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

    @Test
    public void testUpdatePerson_Successful() throws Exception {
        // Arrange
        String lastName = "John";
        String firstName = "Boyd";
        String updatedPayload = "{\"address\":\"54 River St\",\"city\":\"Culver\",\"zip\":\"97451\",\"phone\":\"842-874-7660\",\"email\":\"johnaboyd@wanadoo.com\"}";

        when(personService.updatePerson(eq(lastName), eq(firstName), any(Person.class)))
                .thenReturn(new Person(lastName, firstName, "54 River St", "Culver", "97451","842-874-7660","johnaboyd@wanadoo.com"));

        // Act & Assert
        mockMvc.perform(put("/person")
                        .param("lastName", lastName)
                        .param("firstName", firstName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("John"))
                .andExpect(jsonPath("$.firstName").value("Boyd"))
                .andExpect(jsonPath("$.address").value("54 River St"))
                .andExpect(jsonPath("$.city").value("Culver"))
                .andExpect(jsonPath("$.zip").value("97451"))
                .andExpect(jsonPath("$.phone").value("842-874-7660"))
                .andExpect(jsonPath("$.email").value("johnaboyd@wanadoo.com"));

    }

    @Test
    public void testUpdatePerson_NotFound() throws Exception {
        String lastName = "Unknown";
        String firstName = "Boyd";
        String updatedPayload = "{\"address\":\"54 River St\",\"city\":\"Culver\",\"zip\":\"97451\",\"phone\":\"842-874-7660\",\"email\":\"johnaboyd@wanadoo.com\"}";

        when(personService.updatePerson(eq(lastName), eq(firstName), any(Person.class)))
                .thenThrow(new PersonNotFoundException());

        mockMvc.perform(put("/person")
                        .param("lastName", lastName)
                        .param("firstName", firstName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPayload))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdatePerson_BadRequest() throws Exception {
        String lastName = "John";
        String firstName = "Boyd";
        // Payload invalide (champ manquant)
        String badPayload = "{\"address\":\"54 River St\"}";

        mockMvc.perform(put("/person")
                        .param("lastName", lastName)
                        .param("firstName", firstName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badPayload))
                .andExpect(status().isBadRequest());
    }

}

