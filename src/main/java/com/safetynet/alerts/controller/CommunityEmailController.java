package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.EmailDTO;
import com.safetynet.alerts.service.CommunityEmailService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class CommunityEmailController {

    private final CommunityEmailService communityEmailService;

    @GetMapping("/communityEmail")
    public ResponseEntity<List<EmailDTO>> getEmailByCity(@RequestParam("city") @NotBlank String city){
        List<EmailDTO> emails = communityEmailService.getEmailsByCity(city);
        return ResponseEntity.ok(emails);
    }
}
