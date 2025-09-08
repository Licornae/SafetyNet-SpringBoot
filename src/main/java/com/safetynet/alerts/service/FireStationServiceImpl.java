package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.DataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FireStationServiceImpl implements FireStationService {

    private final DataRepository dataRepository;

    @Autowired
    public FireStationServiceImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public FireStation getFireStation(String address) {
        return dataRepository.getDataContainer().getFirestations().stream()
                .filter(fireStation -> fireStation.getAddress().equals(address))
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

    @Override
    public boolean deleteFireStationByAddress(String address) {
        List<FireStation> fireStations = dataRepository.getDataContainer().getFirestations();
        return fireStations.removeIf(fireStation -> fireStation.getAddress().equals(address));
    }

    @Override
    public int countByStation(String station) {
        return (int) dataRepository.getDataContainer().getFirestations().stream()
                .filter(fireStation -> station.equals(fireStation.getStation()))
                .count();
    }

    @Override
    public boolean deleteFireStationsByStation(String station) {
        List<FireStation> list = dataRepository.getDataContainer().getFirestations();
        int before = list.size();
        list.removeIf(fireStation -> station.equals(fireStation.getStation()));
        int removed = before - list.size();
        log.warn("Deleted {} firestation mappings for station {}", removed, station);
        return removed > 0;
    }
}
