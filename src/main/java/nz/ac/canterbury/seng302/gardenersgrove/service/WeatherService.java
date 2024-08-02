package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.ForecastResponse;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class WeatherService {

    //Retrieved from application-dev.properties
    @Value("${weather.api.key:#{null}}")
    private String apiKey;

    //Retrieved from application-dev.properties
    @Value("${weather.api.url:#{null}}")
    private String apiUrl;

    private static final int FORECAST_LEN = 6;
    private static final String  CUR_WEATHER_URL = "%sweather?q=%s&appid=%s&units=metric";
    private static final String  FORECAST_URL = "%sforecast/daily?q=%s&cnt=" + FORECAST_LEN + "&appid=%s&units=metric";
    private static final String  HISTORIC_WEATHER_URL = "https://history.openweathermap.org/data/2.5/history/city?q=%s&type=hour&start=%s&appid=%s&units=metric";


    private final RestTemplate restTemplate;
    private final CountryCodeService countryCodeService;
    private final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public WeatherService(CountryCodeService countryCodeService, RestTemplate restTemplate) {
        this.countryCodeService = countryCodeService;
        this.restTemplate = restTemplate;
    }

    /**
     * Retrieves the current weather for the specified city and country.
     *
     * @param city the name of the city
     * @param country the name of the country
     *                Will default to Openweathermap.com default country if invalid
     * @return the current weather information, or {@code null} if the city is invalid
     */
    public WeatherResponse getCurrentWeather(String city, String country) {
        if (city == null || city.isBlank()) return null;

        String location = buildLocationString(city, country);
        String url = String.format(CUR_WEATHER_URL, apiUrl, location, apiKey);

        return fetchData(url, WeatherResponse.class);
    }

    /**
     * Retrieves the weather forecast for the specified city and country.
     *
     * @param city the name of the city
     * @param country the name of the country
     *                Will default to Openweathermap.com default country if invalid
     * @return the weather forecast information, or {@code null} if the city is invalid
     */
    public ForecastResponse getForecastWeather(String city, String country) {
        if (city == null || city.isBlank()) return null;

        String location = buildLocationString(city, country);
        String url = String.format(FORECAST_URL, apiUrl, location, apiKey);

        return fetchData(url, ForecastResponse.class);
    }

    /**
     * Returns true if it has rained in the last 2 days
     * @param city  String if the city name to check must be valid
     * @param country   String of the country name to check (must be full name)
 *                      Will default to Openweathermap.com default country if invalid
     * @return  Boolean of false if it has not rained in the last 2 days, true if it has rained
     */
    public Boolean hasRained(String city, String country) {
        long startEpoch = getStartOfDayTwoDaysAgoEpoch();

        if (city == null || city.isBlank()) return null;

        String location = buildLocationString(city, country);
        String url = String.format(HISTORIC_WEATHER_URL, location, startEpoch, apiKey);     //No api url value used here as historic weather has diff url: https://history.openweathermap.org/data/2.5/

        return queryHasRained(url);
    }

    private long getStartOfDayTwoDaysAgoEpoch() {
        // Get the start of the day for two days ago
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        ZonedDateTime startOfDayTwoDaysAgo = twoDaysAgo.atStartOfDay(ZoneId.systemDefault());
        return startOfDayTwoDaysAgo.toEpochSecond();
    }

    private Boolean queryHasRained(String url) {
        try {
            String response = restTemplate.getForObject(url, String.class);
            logger.info("API Response: " + response);
            if (response == null || response.contains("not found")) {
                return null;
            }
            return response.contains("rain");
        } catch (Exception e) {
            logger.error("Failed to fetch data from {}. Cause: {}", url, e.getMessage());
            return null;
        }
    }

    private String buildLocationString(String city, String country) {
        String countryCode = (country != null) ? countryCodeService.getCountryCode(country) : "";
        return city + (countryCode.isEmpty() ? "" : "," + countryCode);
    }

    private <T> T fetchData(String url, Class<T> responseType) {
        try {
            String response = restTemplate.getForObject(url, String.class);
            logger.info("API Response: " + response);
            if (response == null || response.contains("not found")) {
                return null;
            }
            return parseJson(response, responseType);
        } catch (Exception e) {
            logger.error("Failed to fetch data from {}. Cause: {}", url, e.getMessage());
            return null;
        }
    }

    private <T> T parseJson(String json, Class<T> clazz) {
        try {
            logger.info("Response: {}", json);
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing API response: {}", e.getMessage());
            return null;
        }
    }
}