package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.DataNotLoadedException;
import com.safetynet.alerts.exception.DuplicateMedicalRecordException;
import com.safetynet.alerts.exception.MedicalRecordNotFoundException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.DataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Default implementation of MedicalRecordService.
 */
@Slf4j
@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final DataRepository dataRepository;

    @Autowired
    public MedicalRecordServiceImpl(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MedicalRecord getMedicalRecord(String firstName, String lastName) {
        log.debug("getMedicalRecord called with firstName='{}', lastName='{}'", firstName, lastName);

        DataContainer container = dataRepository.getDataContainer();
        if (container == null || container.getMedicalrecords() == null) {
            log.error("getMedicalRecord: DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        MedicalRecord result = container.getMedicalrecords().stream()
                .filter(mr -> mr.getFirstName().equals(firstName) && mr.getLastName().equals(lastName))
                .findFirst()
                .orElse(null);

        if (result == null) {
            log.debug("getMedicalRecord: no record found for '{} {}'", firstName, lastName);
        } else {
            log.debug("getMedicalRecord: record found for '{} {}'", firstName, lastName);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MedicalRecord addMedicalRecord(MedicalRecord medicalRecord) {
        log.debug("addMedicalRecord called for '{} {}'", medicalRecord.getFirstName(), medicalRecord.getLastName());

        List<MedicalRecord> listMR = dataRepository.getDataContainer().getMedicalrecords();

        boolean exists = listMR.stream().anyMatch(mr ->
                mr != null && medicalRecord.getFirstName().equals(mr.getFirstName()) && medicalRecord.getLastName().equals(mr.getLastName()));

        if (exists) {
            log.error("addMedicalRecord: duplicate for '{} {}'", medicalRecord.getFirstName(), medicalRecord.getLastName());
            throw new DuplicateMedicalRecordException("Medical record already exists for: " + medicalRecord.getFirstName() + " " + medicalRecord.getLastName());
        }

        listMR.add(medicalRecord);
        log.debug("addMedicalRecord: created record for '{} {}'", medicalRecord.getFirstName(), medicalRecord.getLastName());

        return medicalRecord;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MedicalRecord updateMedicalRecord(String firstName, String lastName, MedicalRecord updatedMedicalRecord) {
        log.debug("updateMedicalRecord called for '{} {}'", firstName, lastName);

        DataContainer container = dataRepository.getDataContainer();
        if (container == null || container.getMedicalrecords() == null) {
            log.error("updateMedicalRecord: DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        List<MedicalRecord> medicalRecords = container.getMedicalrecords();

        for(MedicalRecord medicalRecord : medicalRecords){
            if(medicalRecord.getFirstName().equals(firstName) && medicalRecord.getLastName().equals(lastName)){
                medicalRecord.setBirthdate(updatedMedicalRecord.getBirthdate());
                medicalRecord.setMedications(updatedMedicalRecord.getMedications());
                medicalRecord.setAllergies(updatedMedicalRecord.getAllergies());
                log.debug("updateMedicalRecord: updated record for '{} {}'", firstName, lastName);

                return medicalRecord;
            }
        }
        log.error("updateMedicalRecord: record not found for '{} {}'", firstName, lastName);
        throw new MedicalRecordNotFoundException("Medical record not found for: " + firstName + " " + lastName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteMedicalRecord(String firstName, String lastName) {
        log.debug("deleteMedicalRecord called for '{} {}'", firstName, lastName);

        DataContainer container = dataRepository.getDataContainer();
        if (container == null || container.getMedicalrecords() == null) {
            log.error("deleteMedicalRecord: DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        List<MedicalRecord> medicalRecords = container.getMedicalrecords();

        boolean mRRemoved = medicalRecords.removeIf(mr ->
                mr != null
                        && firstName.equals(mr.getFirstName())
                        && lastName.equals(mr.getLastName())
        );

        if (mRRemoved) {
            log.debug("deleteMedicalRecord: deleted record(s) for '{} {}'", firstName, lastName);
        } else {
            log.error("deleteMedicalRecord: no record found for '{} {}'", firstName, lastName);
        }

        return mRRemoved;
    }

}
