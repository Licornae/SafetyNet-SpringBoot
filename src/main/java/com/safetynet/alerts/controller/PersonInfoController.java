package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.PersonInfoDTO;
import com.safetynet.alerts.service.PersonInfoService;
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
 * REST controller exposing the /personInfolastName endpoint.
 * Endpoint:
 * - GET /personInfolastName?lastName={value}
 * Returns detailed information for all persons matching the given last name:
 * firstName, lastName, address, age, email, medications, allergies.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class PersonInfoController {

    private final PersonInfoService personInfoService;

    /**
     * Get detailed info for persons with the provided last name.
     *
     * @param lastName must not be blank
     * @return 200 OK with a JSON array of PersonInfoDTO
     */
    @GetMapping("/personInfolastName")
    public ResponseEntity<List<PersonInfoDTO>> getPersonInfoByLastName(@RequestParam("lastName") @NotBlank String lastName){
        log.info("GET /personInfolastName called with lastName='{}'", lastName);

        List<PersonInfoDTO> result = personInfoService.getPersonInfoByLastName(lastName);
        log.info("Found {} person(s) for lastName='{}'", result.size(), lastName);

        return ResponseEntity.ok().header("Result-Info", "LastName not found").body(result);
    }

}
