package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.DataContainer;
import org.springframework.stereotype.Repository;

import java.io.InputStream;

@Repository
public class DataRepository {
    private DataContainer dataContainer;

    public DataRepository() {
        this.loadData();
    }

    private void loadData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data.json");
            dataContainer = objectMapper.readValue(inputStream, DataContainer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DataContainer getDataContainer() {
        return dataContainer;
    }
}

