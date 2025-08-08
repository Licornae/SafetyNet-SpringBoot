package com.safetynet.alerts.service;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FireStationServiceImpl implements FireStationService {

    private final DataRepository dataRepository;

    @Autowired
    public FireStationServiceImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public FireStation getFireStation(String address) {
        return dataRepository.getDataContainer().getFirestations().stream()
                .filter(p -> p.getAddress().equals(address))
                .findFirst()
                .orElse(null);
    }

    @Override
    public FireStation addFireStation(FireStation fireStation) {
        dataRepository.getDataContainer().getFirestations().add(fireStation);
        return fireStation;
    }
}
