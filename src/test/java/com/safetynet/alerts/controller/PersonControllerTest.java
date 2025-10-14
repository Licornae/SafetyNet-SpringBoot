package com.safetynet.alerts.controller;

import com.safetynet.alerts.exception.DuplicatePersonException;
import com.safetynet.alerts.exception.PersonNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    //POST

    @Test
    void testAddANewPerson_Successful() throws Exception {
        // Arrange
        Person newPerson = new Person("Jane","Doe","56 River St", "Culver", "97451", "841-874-7650", "janedoe@email.com");

        when(personService.addPerson(any(Person.class))).thenReturn(newPerson);

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
    void testAddPerson_MissingFirstName_ReturnsBadRequest() throws Exception {
        // Arrange
        Person invalidPerson = new Person("", "Doe", "56 River St", "Culver", "97451", "841-874-7650", "janedoe@email.com");

        // Act & Assert
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPerson))
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testAddPerson_ExistingPerson_ReturnsConflict() throws Exception {
        // Arrange
        Person duplicate = new Person("Jane","Doe","56 River St", "Culver", "97451", "841-874-7650", "janedoe@email.com");
        when(personService.addPerson(any(Person.class)))
                .thenThrow(new DuplicatePersonException("This person already exists: Jane Doe"));

        // Act & Assert
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("This person already exists: Jane Doe"))
                .andExpect(jsonPath("$.path").value("/person"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    //PUT

    @Test
    public void testUpdatePerson_Successful() throws Exception {
        // Arrange
        String firstName = "John";
        String lastName = "Boyd";
        String updatedPayload = "{\"firstName\":\"John\",\"lastName\":\"Boyd\",\"address\":\"54 River St\",\"city\":\"Culver\",\"zip\":\"97451\",\"phone\":\"842-874-7660\",\"email\":\"johnaboyd@wanadoo.com\"}";

        when(personService.updatePerson(eq(firstName), eq(lastName), any(Person.class)))
                .thenReturn(new Person(firstName, lastName, "54 River St", "Culver", "97451","842-874-7660","johnaboyd@wanadoo.com"));

        // Act & Assert
        mockMvc.perform(put("/person/{firstName}/{lastName}", "John", "Boyd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Boyd"))
                .andExpect(jsonPath("$.address").value("54 River St"))
                .andExpect(jsonPath("$.city").value("Culver"))
                .andExpect(jsonPath("$.zip").value("97451"))
                .andExpect(jsonPath("$.phone").value("842-874-7660"))
                .andExpect(jsonPath("$.email").value("johnaboyd@wanadoo.com"));

    }

    @Test
    public void testUpdatePerson_NotFound() throws Exception {
        // Arrange
        String firstName = "John";
        String lastName = "Unknown";
        String updatedPayload = "{\"firstName\":\"John\",\"lastName\":\"Unknown\",\"address\":\"54 River St\",\"city\":\"Culver\",\"zip\":\"97451\",\"phone\":\"842-874-7660\",\"email\":\"johnaboyd@wanadoo.com\"}";

        when(personService.updatePerson(eq(firstName), eq(lastName), any(Person.class)))
                .thenThrow(new PersonNotFoundException());

        // Act & Assert
        mockMvc.perform(put("/person/{firstName}/{lastName}", "John", "Unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPayload))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdatePerson_BadRequest() throws Exception {
        // Arrange
        String firstName = "John";
        String lastName = "Boyd";
        // Payload invalide (champ manquant)
        String badPayload = "{\"firstName\":\"John\",\"lastName\":\"Boyd\",\"address\":\"54 River St\"}";

        // Act & Assert
        mockMvc.perform(put("/person/{firstName}/{lastName}", "John", "Boyd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdatePerson_PathBodyMismatch_ReturnsBadRequest() throws Exception {
        String badPayload = """
            {"firstName":"John","lastName":"Different","address":"54 River St","city":"Culver","zip":"97451","phone":"842-874-7660","email":"johnaboyd@wanadoo.com"}
        """;
        mockMvc.perform(put("/person/{firstName}/{lastName}", "John", "Boyd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badPayload))
                .andExpect(status().isBadRequest())

                .andExpect(jsonPath("$.message").value("Path {firstName,lastName} must match body."))
                .andExpect(jsonPath("$.path").value("/person/John/Boyd"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    //DELETE

    @Test
    public void testDeletePerson_Successful() throws Exception {
        // Arrange
        String firstName = "John";
        String lastName = "Boyd";
        when(personService.deletePerson(firstName, lastName)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/person/{firstName}/{lastName}", firstName, lastName))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeletePerson_NotFound_Returns404() throws Exception {
        String firstName = "Jane";
        String lastName = "Unknown";

        when(personService.deletePerson(firstName, lastName)).thenReturn(false);

        mockMvc.perform(delete("/person/{firstName}/{lastName}", firstName, lastName))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

}

