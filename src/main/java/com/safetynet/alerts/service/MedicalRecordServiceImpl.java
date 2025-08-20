package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.MedicalRecordNotFoundException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public MedicalRecord addMedicalRecord(MedicalRecord medicalRecord) {
        dataRepository.getDataContainer().getMedicalrecords().add(medicalRecord);
        return medicalRecord;
    }

    @Override
    public MedicalRecord updateMedicalRecord(String firstName, String lastName, MedicalRecord updatedMedicalRecord) {
        List<MedicalRecord> medicalRecords = dataRepository.getDataContainer().getMedicalrecords();

        for(MedicalRecord medicalRecord : medicalRecords){
            if(medicalRecord.getFirstName().equals(firstName) && medicalRecord.getLastName().equals(lastName)){
                medicalRecord.setBirthdate(updatedMedicalRecord.getBirthdate());
                medicalRecord.setMedications(updatedMedicalRecord.getMedications());
                medicalRecord.setAllergies(updatedMedicalRecord.getMedications());
                return medicalRecord;
            }
        }
        throw new MedicalRecordNotFoundException("Medical record not found for: " + firstName + " " + lastName);
    }

    @Override
    public boolean deleteMedicalRecord(String firstName, String lastName) {
        List<MedicalRecord> medicalRecords = dataRepository.getDataContainer().getMedicalrecords();
        return medicalRecords.removeIf(medicalRecord -> medicalRecord.getFirstName().equals(firstName)
                && medicalRecord.getLastName().equals(lastName));
    }

}
