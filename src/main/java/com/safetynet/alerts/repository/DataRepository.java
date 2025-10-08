package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.DataContainer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.InputStream;

/**
 * Simple repository loading DataContainer from classpath resource data.json.
 */
@Slf4j
@Getter
@Repository
public class DataRepository {

    private volatile DataContainer dataContainer;

    public DataRepository() {
        this.loadData();
    }

    private void loadData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data.json");
            dataContainer = objectMapper.readValue(inputStream, DataContainer.class);

            log.info("Data loaded: persons={}, medicalRecords={}, stations={}",
                    dataContainer.getPersons().size(),
                    dataContainer.getMedicalrecords().size(),
                    dataContainer.getFirestations().size());

        } catch (Exception e) {
            log.error("Failed to load 'data.json': {}", e.getMessage(), e);
            dataContainer = null;
        }
    }

    /**
     * Reloads data from the classpath resource.
     */
    public void reloadData() {
        loadData();
    }

}

