package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public FireStation updateStationAddress(String address, FireStation updatedFireStationAddress) {
        List<FireStation> fireStations = dataRepository.getDataContainer().getFirestations();

        for (FireStation fireStation : fireStations){
            if (fireStation.getAddress().equals(address)){
                fireStation.setStation(updatedFireStationAddress.getStation());
                return fireStation;
            }
        }
        throw new AddressNotFoundException("Address not found : " + address);
    }
}
