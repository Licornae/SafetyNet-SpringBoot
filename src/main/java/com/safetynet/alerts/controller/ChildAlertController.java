package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.ChildAlertDTO;
import com.safetynet.alerts.service.ChildAlertService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ChildAlertController {

    private final ChildAlertService childAlertService;

    @GetMapping("/childAlert")
    public ResponseEntity<?> getChildAlertByAddress(@RequestParam("address") @NotBlank String address){

        List<ChildAlertDTO> result = childAlertService.getChildrenByAddress(address);

        if (result.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(result);
    }
}
