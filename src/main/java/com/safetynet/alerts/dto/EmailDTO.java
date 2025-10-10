package com.safetynet.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing an email address entry.
 * Fields:
 * - email (String): a contact email address.
 * Usage:
 * - Returned as items in the response for GET /communityEmail?city={city}.
 * - Can be used anywhere a lean representation of a person's email is required
 *   without exposing other personal data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {
    private String email;
}
