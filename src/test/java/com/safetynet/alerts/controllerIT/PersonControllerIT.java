package com.safetynet.alerts.controllerIT;

import com.safetynet.alerts.controller.PersonController;
import com.safetynet.alerts.exception.DuplicatePersonException;
import com.safetynet.alerts.exception.PersonNotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;



@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerIT {

    @Autowired
    private PersonController controller;

    @Autowired
    private DataRepository dataRepository;

    @BeforeEach
    public void reload() {
        dataRepository.reloadData();
    }

    // POST

    @Test
    public void addPerson_Successful() {
        Person newPerson = new Person("Jane", "Doe", "56 River St", "Culver", "97451", "841-874-7650", "jane.doe@email.com"
        );

        ResponseEntity<?> response = controller.addPerson(newPerson);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isInstanceOf(Person.class);

        Person body = (Person) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getFirstName()).isEqualTo("Jane");
        assertThat(body.getLastName()).isEqualTo("Doe");
        assertThat(body.getAddress()).isEqualTo("56 River St");
        assertThat(body.getCity()).isEqualTo("Culver");
        assertThat(body.getZip()).isEqualTo("97451");
        assertThat(body.getPhone()).isEqualTo("841-874-7650");
        assertThat(body.getEmail()).isEqualTo("jane.doe@email.com");
    }

    @Test
    public void addPerson_Duplicate_ThrowsDuplicatePersonException() {
        Person existing = new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jaboyd@email.com"
        );

        assertThatThrownBy(() -> controller.addPerson(existing))
                .isInstanceOf(DuplicatePersonException.class);
    }

    // PUT

    @Test
    public void updatePerson_Successful() {
        String firstName = "John";
        String lastName = "Boyd";
        Person payload = new Person(
                firstName, lastName,
                "54 River St", "Culver", "97451",
                "842-874-7660", "john.boyd@wanadoo.com"
        );

        ResponseEntity<Person> response = controller.updatePerson(firstName, lastName, payload);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        Person body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getFirstName()).isEqualTo("John");
        assertThat(body.getLastName()).isEqualTo("Boyd");
        assertThat(body.getAddress()).isEqualTo("54 River St");
        assertThat(body.getCity()).isEqualTo("Culver");
        assertThat(body.getZip()).isEqualTo("97451");
        assertThat(body.getPhone()).isEqualTo("842-874-7660");
        assertThat(body.getEmail()).isEqualTo("john.boyd@wanadoo.com");
    }

    @Test
    public void updatePerson_NotFound_ThrowsPersonNotFoundException() {
        String firstName = "Jane";
        String lastName = "Unknown";
        Person payload = new Person(
                firstName, lastName,
                "unknown", "unknown", "00000",
                "000-000-0000", "unknown@mail.com"
        );

        assertThatThrownBy(() -> controller.updatePerson(firstName, lastName, payload))
                .isInstanceOf(PersonNotFoundException.class);
    }

    @Test
    public void updatePerson_PathBodyMismatch_ThrowsIllegalArgumentException() {
        String pathFirst = "John";
        String pathLast = "Boyd";
        Person payload = new Person(
                "John", "Mismatch",
                "54 River St", "Culver", "97451",
                "842-874-7660", "john.boyd@wanadoo.com"
        );

        assertThatThrownBy(() -> controller.updatePerson(pathFirst, pathLast, payload))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // DELETE

    @Test
    public void deletePerson_Successful() {
        String firstName = "John";
        String lastName = "Boyd";

        ResponseEntity<Void> response = controller.deletePerson(firstName, lastName);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void deletePerson_NotFound_Returns404() {
        String firstName = "Jane";
        String lastName = "Unknown";

        ResponseEntity<Void> response = controller.deletePerson(firstName, lastName);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }
}
