package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final DataRepository dataRepository;

    @Autowired
    public MedicalRecordServiceImpl(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    @Override
    public MedicalRecord getMedicalRecord(String firstName, String lastName) {
        return dataRepository.getDataContainer().getMedicalrecords().stream()
                .filter(medicalRecord -> medicalRecord.getFirstName().equals(firstName) &&
                        medicalRecord.getLastName().equals(lastName)).findFirst().orElse(null);
    }

}
