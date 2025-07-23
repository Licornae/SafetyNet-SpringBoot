package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @MockBean
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
}

