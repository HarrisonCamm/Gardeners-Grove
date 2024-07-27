package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class WeatherService {

    //Retrieved from application-dev.properties
    @Value("${weather.api.key:#{null}}")
    private String apiKey;

    //Retrieved from application-dev.properties
    @Value("${weather.api.url:#{null}}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    Logger logger = LoggerFactory.getLogger(WeatherService.class);

    ObjectMapper objectMapper = new ObjectMapper();

    private final CountryCodeService countryCodeService;

    @Autowired
    public WeatherService(CountryCodeService countryCodeService) {
        this.restTemplate = new RestTemplate();
        this.countryCodeService = countryCodeService;
    }

    //test use constructor
    public WeatherService(CountryCodeService countryCodeService, RestTemplate restTemplate) {
        this.countryCodeService = countryCodeService;
        this.restTemplate = restTemplate;
    }

    /**
     * Parses the JSON response from the weather API into a WeatherResponse object
     * Attribution: Based off of SENG301 Lab 6 example code
     *
     * @param stringResult The JSON response from the weather API as a String
     * @return A WeatherResponse object representing the JSON response
     */
    private WeatherResponse parseWeatherJson(String stringResult) {
        WeatherResponse weatherResponse = null;
        try {
            logger.info(stringResult);
            weatherResponse = objectMapper.readValue(stringResult, WeatherResponse.class);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing API response", e);
        }
        logger.info("Weather response: " + weatherResponse);
        return weatherResponse;
    }

    /**
     * Fetches the current weather for a given city and country
     *
     * @param city    A string of City name to fetch weather for not case-sensitive
     * @param country A string of full country name to fetch weather for not case-sensitive disregarded if invalid
     * @return A null object if the API call fails or a WeatherResponse object representing the current weather
     */
    public WeatherResponse getCurrentWeather(String city, String country) {

        //Uses the country code service to get the country code (2 letter ) for the given country name
        String countryCode = countryCodeService.getCountryCode(country);

        //Combines the city and country code to form the location string used in API call
        String location = city + (countryCode.isEmpty() ? "" : "," + countryCode);
        logger.info("Fetching current weather for " + location + " from " + apiUrl);

        //Attempts to retrieve a response from api, any cause of failure results in null return value
        try {
            String url = String.format("%sweather?q=%s&appid=%s&units=metric", apiUrl, location, apiKey);
            String response = restTemplate.getForObject(url, String.class);
            return parseWeatherJson(response);
        } catch (Exception e) {
            logger.info("Failed to fetch current weather for " + location + " from " + apiUrl);
            return null;
        }
    }

}