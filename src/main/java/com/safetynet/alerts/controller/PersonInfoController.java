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


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class PersonInfoController {

    private final PersonInfoService personInfoService;

    @GetMapping("/personInfolastName")
    public ResponseEntity<List<PersonInfoDTO>> getPersonInfoByLastName(@RequestParam("lastName") @NotBlank String lastName){
        List<PersonInfoDTO> result = personInfoService.getPersonInfoByLastName(lastName);
        return ResponseEntity.ok().header("Result-Info", "LastName not found").body(result);
    }

}
