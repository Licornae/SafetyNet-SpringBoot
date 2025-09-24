package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.FireDTO;
import com.safetynet.alerts.service.FireService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class FireController {
    private final FireService fireService;

    @GetMapping(value = "/fire", params = "address")
    public ResponseEntity<FireDTO> getResidentsByAddress(
            @RequestParam @NotBlank String address) {
        return ResponseEntity.ok(fireService.getResidentsByAddress(address));
    }
}
