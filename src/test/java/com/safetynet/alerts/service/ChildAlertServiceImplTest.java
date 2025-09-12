package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.ChildAlertDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChildAlertServiceImplTest {

    @Autowired
    DataRepository dataRepository;

    @BeforeEach
    void resetData() {
        dataRepository.reloadData();
    }

    @Autowired
    ChildAlertService childAlertService;

    @Test
    public void testGetChildAlertByAddress_returnsChildrenInfoAndFamilyMembers(){

        String address = "1509 Culver St";

        List<ChildAlertDTO> children = childAlertService.getChildrenByAddress(address);

        assertNotNull(children, "Result should not be null");

        assertTrue(children.stream().allMatch(child -> child.getAge() <= 18),
                "Every returned person must be 18 or younger");

        assertTrue(children.stream().anyMatch(child ->
                        "Tenley".equals(child.getFirstName()) && "Boyd".equals(child.getLastName())),
                "Tenley Boyd should be included among the children at 1509 Culver St");

        children.forEach(child -> {
            assertNotNull(child.getFamilyMembers(), "A child can't live alone");

            assertEquals(4, child.getFamilyMembers().size(),
                    "This child lives with 3 adults and another child");

            assertTrue(child.getFamilyMembers().stream()
                            .noneMatch(familyMember -> familyMember.getFirstName().equals(child.getFirstName()) &&
                                    familyMember.getLastName().equals(child.getLastName())),
                    "The child must not be count as family member");
        });
    }

    @Test
    void testGetChildrenByAddress_unknownAddress_returnsEmptyList() {
        List<ChildAlertDTO> children = childAlertService.getChildrenByAddress("Unknown Address");
        assertNotNull(children, "Result should not be null");
        assertTrue(children.isEmpty(), "Unknown address should return an empty list");
    }

    @Test
    void testGetChildrenByAddress_blankAddress_throws() {
        assertThrows(AddressNotFoundException.class,
                () -> childAlertService.getChildrenByAddress(" "),
                "Blank address should throw AddressNotFoundException");
    }

}
