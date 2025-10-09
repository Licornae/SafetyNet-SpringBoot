package com.safetynet.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing a child living at an address and their family members.
 * Fields:
 * - firstName (String): child's first name.
 * - lastName (String): child's last name.
 * - age (int): child's age (≤ 18).
 * - familyMembers (List<FamilyMembersDTO>): family members living at the same address.
 * Usage:
 * - Element of the response for GET /childAlert?address={value}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildAlertDTO {
    private String firstName;
    private String lastName;
    private int age;
    private List<FamilyMembersDTO> familyMembers;
}
