package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.EmailDTO;
import com.safetynet.alerts.service.CommunityEmailService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * REST controller exposing the /communityEmail endpoint.
 * Endpoint:
 * - GET /communityEmail?city={city}
 * Returns the distinct list of resident email addresses for a given city.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class CommunityEmailController {

    private final CommunityEmailService communityEmailService;

    /**
     * Get the distinct list of email addresses for the provided city.
     *
     * @param city city name must not be blank
     * @return 200 OK with the list of EmailDTO
     */
    @GetMapping("/communityEmail")
    public ResponseEntity<List<EmailDTO>> getEmailByCity(@RequestParam("city") @NotBlank String city){
        log.info("GET /communityEmail called with city='{}'", city);

        List<EmailDTO> emails = communityEmailService.getEmailsByCity(city);
        log.info("communityEmail: returning {} email(s) for city='{}'", emails.size(), city);

        return ResponseEntity.ok(emails);
    }
}
