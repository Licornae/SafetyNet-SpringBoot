package com.safetynet.alerts.service;


import com.safetynet.alerts.dto.EmailDTO;
import com.safetynet.alerts.exception.CityNotFoundException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommunityEmailServiceImplUnitTest {

    @Mock
    private DataRepository dataRepository;

    @InjectMocks
    private CommunityEmailServiceImpl communityEmailService;

    private DataContainer dataContainer;

    @BeforeEach
    void setUp() {
        List<Person> persons = new ArrayList<>(of(
                new Person("John",  "Boyd",  "1509 Culver St", "Culver", "97451", "841-874-6512", "john@email.com"),
                new Person("Jacob", "Boyd",  "1509 Culver St", "Culver", "97451", "841-874-6513", "jacob@email.com"),
                new Person("Jane",  "Doe",   "56 River St",    "Culver", "97451", "841-874-7650", "jane@corp.com"),
                new Person("Foo",   "Bar",   "1 Nowhere",      "Paris",  "75000", "000-000",      "foo@bar.com"),

                new Person("John",  "Clone", "2 Same St",      "Culver", "97451", "111-111",      "john@email.com")
        ));
        dataContainer = new DataContainer();
        dataContainer.setPersons(persons);

        when(dataRepository.getDataContainer()).thenReturn(dataContainer);
    }

    @Test
    void getEmailsByCity_ReturnsUniqueEmailsForCity() {
        List<EmailDTO> emails = communityEmailService.getEmailsByCity("Culver");
        assertNotNull(emails);

        assertEquals(3, emails.size(),"emails are unique");

        List<String> values = emails.stream().map(EmailDTO::getEmail).toList();
        assertTrue(values.containsAll(of("john@email.com", "jacob@email.com", "jane@corp.com")));

    }

    @Test
    void getEmailsByCity_UnknownCity_ThrowsCityNotFound() {
        CityNotFoundException exception = assertThrows(
                CityNotFoundException.class,
                () -> communityEmailService.getEmailsByCity("Unknown")
        );
        assertEquals("Unknown city", exception.getMessage());
    }
}
