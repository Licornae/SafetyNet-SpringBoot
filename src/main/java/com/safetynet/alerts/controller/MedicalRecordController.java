package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService){
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping("/medicalRecord")
    public MedicalRecord getMedicalRecord(@RequestParam String firstName, @RequestParam String lastName){
        return medicalRecordService.getMedicalRecord(firstName,lastName);
    }
}
