package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.ChildAlertDTO;
import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChildAlertServiceImplUnitTest {

    @Mock
    PeopleWithAgeService peopleWithAgeService;

    @InjectMocks
    ChildAlertServiceImpl service;

    private List<PersonDTO> dataset() {
        return of(
                new PersonDTO("John",    "Boyd",   41, "1509 Culver St", "841-874-6512"),
                new PersonDTO("Jacob",   "Boyd",   36, "1509 Culver St", "841-874-6513"),
                new PersonDTO("Tenley",  "Boyd",   13, "1509 Culver St", "841-874-6512"),
                new PersonDTO("Roger",   "Boyd",    8, "1509 Culver St", "841-874-6512"),
                new PersonDTO("Felicia", "Boyd",   39, "1509 Culver St", "841-874-6544"),

                new PersonDTO("Jonanathan","Marrack",36,"29 15th St","841-874-6513"),
                new PersonDTO("Tessa",   "Carman", 13, "834 Binoc Ave", "841-874-6512"),
                new PersonDTO("Peter",   "Duncan", 25, "644 Gershwin Cir", "841-874-6512")
        );
    }

    @Test
    public void getChildrenByAddress_returnsTenleyAndRoger_withFamilyMembers() {
        when(peopleWithAgeService.listAll()).thenReturn(dataset());

        List<ChildAlertDTO> children = service.getChildrenByAddress("1509 Culver St");

        assertNotNull(children);
        assertEquals(2, children.size(), "2 children at this address");

        // Tenley
        ChildAlertDTO tenley = children.stream()
                .filter(child -> "Tenley".equals(child.getFirstName()))
                .findFirst().orElseThrow();
        assertEquals("Boyd", tenley.getLastName());
        assertEquals(13, tenley.getAge());
        assertNotNull(tenley.getFamilyMembers());
        assertEquals(4, tenley.getFamilyMembers().size(), "John, Jacob, Roger, Felicia");

        // Roger
        ChildAlertDTO roger = children.stream()
                .filter(child -> "Roger".equals(child.getFirstName()))
                .findFirst().orElseThrow();
        assertEquals("Boyd", roger.getLastName());
        assertEquals(8, roger.getAge());
        assertNotNull(roger.getFamilyMembers());
        assertEquals(4, roger.getFamilyMembers().size(), "John, Jacob, Tenley, Felicia");
    }

    @Test
    public void getChildrenByAddress_UnknownAddress_ThrowsAddressNotFound() {
        assertThrows(AddressNotFoundException.class,
                () -> service.getChildrenByAddress("Unknown Address"),
                "Unknown address should throw AddressNotFoundException");
    }

    @Test
    public void getChildrenByAddress_ExistingAddressOnlyAdults_ReturnsEmptyList() {
        when(peopleWithAgeService.listAll()).thenReturn(dataset());

        List<ChildAlertDTO> children = service.getChildrenByAddress("644 Gershwin Cir");
        assertNotNull(children, "Result should not be null");
        assertTrue(children.isEmpty(), "Existing address with adults only should return an empty list");
    }

    @Test
    public void getChildrenByAddress_BlankAddress_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getChildrenByAddress(" "),
                "Blank address should throw IllegalArgumentException");
    }

}