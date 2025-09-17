package com.safetynet.alerts.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.service.PhoneAlertService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class PhoneAlertController {

    private final PhoneAlertService phoneAlertService;

    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhones(@RequestParam("firestation") @NotBlank String station) {
        List<String> phones = phoneAlertService.getPhonesByFirestation(station);
        return ResponseEntity.ok(phones);
    }
}
