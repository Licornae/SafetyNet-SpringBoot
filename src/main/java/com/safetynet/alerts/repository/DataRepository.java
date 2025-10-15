package com.safetynet.alerts.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.core.io.ClassPathResource;
import com.safetynet.alerts.model.DataContainer;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;

/**
 * Repository responsible for loading, storing, and persisting application data.
 * The repository initializes its data from a JSON resource located either in the classpath
 * or on the filesystem, depending on configuration. It also supports persistence
 * by saving modifications back to an external file when enabled.
 */
@Slf4j
@Getter
@Repository
public class DataRepository {

    private volatile DataContainer dataContainer;
    private final ObjectMapper objectMapper;

    @Value("${data.original.path}")
    private String originalPathProp;

    @Value("${data.updated.path:}")
    private String updatedPathProp;

    @Value("${data.persistence.enabled:false}")
    private boolean persistenceEnabled;

    public DataRepository() {
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT); // Local ObjectMapper configured for formatted JSON output
    }

    /**
     * Post-construct initialization invoked by the Spring container.
     * This method ensures that the repository is field with data
     * immediately after bean creation, before any other component accesses it.
     * It attempts to load from the persisted "updated" file if available and enabled,
     * otherwise falls back to the original bundled resource.
     */

    @PostConstruct
    public void init() {
        log.info("Effective properties -> original='{}', updated='{}', persistenceEnabled={}", originalPathProp, updatedPathProp, persistenceEnabled);
        loadAtStartup();
    }

    /**
     * Load data at application startup.
     * Behavior:
     *  - If persistence is enabled and an updated file is present on disk, load from it.
     *  - Otherwise, load the original data.
     *  - Any exception during loading is logged, and dataContainer is set to null.
     *  The loaded dataContainer is stored in a volatile field to ensure visibility across threads.
     */
    private void loadAtStartup() {
        try {
            if (persistenceEnabled && isFile(updatedPathProp) && Files.exists(Path.of(updatedPathProp))) {
                dataContainer = objectMapper.readValue(new File(updatedPathProp), DataContainer.class);// Load from updated persisted file on disk.
                log.info("Data loaded from updated file: {}", updatedPathProp);
                return;
            }
            dataContainer = readOriginal(); // Fall back to the original resource
            log.info("Data loaded from original: {}", originalPathProp);
        } catch (Exception e) {
            log.error("Failed to load data: {}", e.getMessage(), e);
            dataContainer = null;
        }
    }

    private boolean isFile(String path) {
        return path != null && !path.isBlank() && !isClasspath(path);
    }

    /**
     * Read the original data source defined by originalPathProp.
     * Supports:
     *  - classpath:<resource> notation -> loads resource from classpath as InputStream
     *  - file path -> loads JSON from the filesystem
     *
     * @return deserialized DataContainer from the original source
     */
    private DataContainer readOriginal() throws Exception {
        if (isClasspath(originalPathProp)) {

            String classpath = originalPathProp.replace("classpath:", "");

            // Try-with-resources ensures the stream is closed after reading.
            try (InputStream inputStream = new ClassPathResource(classpath).getInputStream()) {
                return objectMapper.readValue(inputStream, DataContainer.class);
            }
        } else {
            // Read from external file path
            return objectMapper.readValue(new File(originalPathProp), DataContainer.class);
        }
    }

    private boolean isClasspath(String path) {
        return path != null && path.startsWith("classpath:");
    }

    /**
     * Reloads data from the classpath resource.
     */
    public void reloadData() {
        try {
            dataContainer = readOriginal();
            log.info("Data reloaded from original (tests): {}", originalPathProp);
        } catch (Exception e) {
            log.error("Failed to reload original: {}", e.getMessage(), e);
        }
    }

    /**
     * Persists the current DataContainer state to disk if persistence is enabled.
     * Behavior:
     *  - No action is performed when persistence is disabled.
     *  - Ensures thread safety using the synchronized keyword to prevent concurrent writes.
     *  - Validates that the configured updatedPathProp points to a valid file path.
     *  - Serializes and writes the current dataContainer as formatted JSON using Jackson.
     */
    public synchronized void saveData() {
        // Skip persistence when disabled (typically in test environments)
        if (!persistenceEnabled) {
            log.debug("Persistence disabled, skipping save.");
            return;
        }
        try {
            // Validate that updatedPathProp represents a valid file system path
            if (!isFile(updatedPathProp)) {
                log.warn("data.updated.path is not a file path. Please set an absolute file path. Current: {}", updatedPathProp);
                return;
            }

            // Serialize and write the DataContainer to the specified file as JSON
            File target = new File(updatedPathProp);
            objectMapper.writeValue(target, dataContainer);
            log.info("Data persisted to {}", updatedPathProp);
        } catch (Exception e) {
            log.error("Failed to persist data to {}: {}", updatedPathProp, e.getMessage(), e);
        }
    }
}

