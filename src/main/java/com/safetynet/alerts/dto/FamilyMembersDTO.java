package com.safetynet.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Presentation model for a family member in child alert responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMembersDTO {
    private String firstName;
    private String lastName;
    private String ageCategory; // "Child" or "Adult"
}
