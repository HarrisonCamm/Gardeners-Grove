package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

@Service
public class PlantFamilyService {
    Logger logger = LoggerFactory.getLogger(PlantFamilyService.class);
    private final Map<String, String> plantFamilies;

    @Autowired
    public PlantFamilyService(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        Resource resource = resourceLoader.getResource("classpath:static/json/plantFamilyNames.json");
        this.plantFamilies = loadPlantFamilies(resource, objectMapper);
    }

    public String getPlantFamily(String family) {
        String commonName = plantFamilies.getOrDefault(family, family);
        return commonName.isEmpty() ? family : commonName;
    }

    private Map<String, String> loadPlantFamilies(Resource resource, ObjectMapper objectMapper) {
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException e) {
            logger.info("Error loading plant family common names", e);
        }
        return Collections.emptyMap();
    }
}
