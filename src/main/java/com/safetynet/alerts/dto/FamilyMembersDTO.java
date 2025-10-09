package com.safetynet.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a family member in childAlert responses.
 * Fields:
 * - firstName (String): first name.
 * - lastName (String): last name.
 * - ageCategory (String): age category ("Child" or "Adult").
 * Usage:
 * - Element of ChildAlertDTO.familyMembers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMembersDTO {
    private String firstName;
    private String lastName;
    private String ageCategory; // "Child" or "Adult"
}
