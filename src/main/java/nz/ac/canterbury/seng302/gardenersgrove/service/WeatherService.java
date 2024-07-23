package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WeatherService {
    @Value("${weather.api.key:#{null}}")
    private String apiKey;

    @Value("${weather.api.url:#{null}}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final CountryCodeService countryCodeService;

    public WeatherService(CountryCodeService countryCodeService) {
        this.countryCodeService = countryCodeService;
    }

    private WeatherResponse parseWeatherJson(JsonNode node) {
        WeatherResponse weatherResponse = new WeatherResponse();
        SimpleDateFormat dayDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);    //Format to for day of the week
        SimpleDateFormat dateDateFormat = new SimpleDateFormat("dd/MM/YYYY", Locale.ENGLISH);    //Format to for date
        weatherResponse.setDayOfWeek(dayDateFormat.format(new Date(node.get("dt").asLong() * 1000)));
        weatherResponse.setDate(dateDateFormat.format(new Date(node.get("dt").asLong() * 1000)));
        weatherResponse.setDescription(node.get("weather").get(0).get("description").asText());
        weatherResponse.setIcon(node.get("weather").get(0).get("icon").asText());
        weatherResponse.setTemperature(node.get("main").get("temp").asText());
        weatherResponse.setHumidity(node.get("main").get("humidity").asText());

        return weatherResponse;
    }

    public WeatherResponse getCurrentWeather(String city, String country) {
        String countryCode = countryCodeService.getCountryCode(country);
        String location = city + (countryCode.isEmpty() ? "" : "," + countryCode);

        logger.info("Fetching current weather for " + location + " from " + apiUrl);
        try {
            String url = String.format("%sweather?q=%s&appid=%s&units=metric", apiUrl, location, apiKey);
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response);
            return parseWeatherJson(node);
        } catch (Exception e) {
            logger.info("Failed to fetch current weather for " + location + " from " + apiUrl);
            return null;
        }
    }

    public List<WeatherResponse> getForecast(String city, String country) {
        String countryCode = countryCodeService.getCountryCode(country);
        String location = city + (countryCode.isEmpty() ? "" : "," + countryCode);

        logger.info("Fetching forecast for " + location + " from " + apiUrl);
        try {
            String url = String.format("%sforecast?q=%s&appid=%s&units=metric", apiUrl, location, apiKey);
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            JsonNode listNode = rootNode.get("list");
            List<WeatherResponse> forecast = new ArrayList<>();
            for (JsonNode forecastNode : listNode) {
                forecast.add(parseWeatherJson(forecastNode));
            }
            return forecast;
        } catch (Exception e) {
            logger.info("Failed to fetch forecast for " + location + " from " + apiUrl);
            return null;
        }
    }
}
