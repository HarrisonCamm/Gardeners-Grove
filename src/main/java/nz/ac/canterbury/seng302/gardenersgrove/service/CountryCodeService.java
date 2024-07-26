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
public class CountryCodeService {
    Logger logger = LoggerFactory.getLogger(CountryCodeService.class);
    private final Map<String, String> countryCodes;

    @Autowired
    public CountryCodeService(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        Resource resource = resourceLoader.getResource("classpath:static/json/countryCodes.json");
        this.countryCodes = loadCountryCodes(resource, objectMapper);
    }

    public String getCountryCode(String country) {
        return countryCodes.getOrDefault(country.toLowerCase(), "");
    }

    private Map<String, String> loadCountryCodes(Resource resource, ObjectMapper objectMapper) {
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException e) {
            logger.info("Error loading country codes", e);
        }
        return Collections.emptyMap();
    }
}
