package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.exception.DuplicateFireStationException;
import com.safetynet.alerts.exception.StationNotFoundException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.DataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.safetynet.alerts.exception.DataNotLoadedException;
import java.util.List;

/**
 * Default implementation of FireStationService.
 */
@Slf4j
@Service
public class FireStationServiceImpl implements FireStationService {

    private final DataRepository dataRepository;

    @Autowired
    public FireStationServiceImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FireStation getFireStation(String address) {
        log.debug("getFireStation called with address='{}'", address);

        DataContainer container = dataRepository.getDataContainer();
        if (container == null || container.getFirestations() == null) {
            log.error("getFireStation: DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        FireStation result = container.getFirestations().stream()
                .filter(fireStation -> fireStation.getAddress().equals(address))
                .findFirst()
                .orElse(null);

        if (result == null) {
            log.debug("getFireStation: no mapping found for address='{}'", address);
        } else {
            log.debug("getFireStation: mapping found for address='{}' -> station='{}'", address, result.getStation());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FireStation addFireStation(FireStation fireStation) {
        log.debug("addFireStation called for address='{}', station='{}'", fireStation.getAddress(), fireStation.getStation());

        DataContainer container = dataRepository.getDataContainer();

        List<FireStation> list = container.getFirestations();
        boolean exists = list.stream().anyMatch(fireStation1 ->
                fireStation1 != null && fireStation.getAddress().equals(fireStation1.getAddress()));

        if (exists) {
            log.warn("addFireStation: duplicate mapping for address='{}'", fireStation.getAddress());
            throw new DuplicateFireStationException("This address already refers to a station: " + fireStation.getAddress());
        }

        list.add(fireStation);
        log.info("addFireStation: created mapping address='{}' -> station='{}'", fireStation.getAddress(), fireStation.getStation());

        return fireStation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FireStation updateStationAddress(String address, FireStation updatedFireStationAddress) {
        log.debug("updateStationAddress called for address='{}' -> newStation='{}'", address, updatedFireStationAddress.getStation());

        DataContainer container = dataRepository.getDataContainer();
        if (container == null || container.getFirestations() == null) {
            log.error("updateStationAddress: DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        List<FireStation> fireStations = container.getFirestations();

        for (FireStation fireStation : fireStations) {
            if (fireStation.getAddress().equals(address)) {
                String oldStation = fireStation.getStation();
                fireStation.setStation(updatedFireStationAddress.getStation());
                log.info("updateStationAddress: address='{}' station updated '{}' -> '{}'", address, oldStation, fireStation.getStation());

                return fireStation;
            }
        }
        log.warn("updateStationAddress: address not found '{}'", address);
        throw new AddressNotFoundException("Address not found : " + address);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFireStationByAddress(String address) {
        log.debug("deleteFireStationByAddress called for address='{}'", address);

        DataContainer container = dataRepository.getDataContainer();
        if (container == null || container.getFirestations() == null) {
            log.error("deleteFireStationByAddress: DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        List<FireStation> fireStations = container.getFirestations();
        boolean removed = fireStations.removeIf(fireStation -> address.equals(fireStation.getAddress()));

        if (!removed) {
            log.warn("deleteFireStationByAddress: no mapping found for address='{}'", address);
            throw new AddressNotFoundException("Address not found : " + address);
        }
        log.info("deleteFireStationByAddress: deleted mapping for address='{}'", address);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countByStation(String station) {
        log.debug("countByStation called for station='{}'", station);

        DataContainer container = dataRepository.getDataContainer();
        if (container == null || container.getFirestations() == null) {
            log.error("countByStation: DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        int count = (int) container.getFirestations().stream()
                .filter(fireStation -> station.equals(fireStation.getStation()))
                .count();

        log.debug("countByStation: station='{}' has {} mapping(s)", station, count);
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFireStationsByStation(String station) {
        log.debug("deleteFireStationsByStation called for station='{}'", station);

        DataContainer container = dataRepository.getDataContainer();
        if (container == null || container.getFirestations() == null) {
            log.error("deleteFireStationsByStation: DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        List<FireStation> list = container.getFirestations();

        int before = list.size();
        list.removeIf(fireStation -> station.equals(fireStation.getStation()));
        int removed = before - list.size();

        if (removed == 0) {
            log.warn("deleteFireStationsByStation: no mappings found for station='{}'", station);
            throw new StationNotFoundException("No mapping found for station: " + station);
        }
        log.info("deleteFireStationsByStation: deleted {} mapping(s) for station='{}'", removed, station);

        return true;
    }
}
