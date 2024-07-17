package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getCurrentWeather(String location) {
        try {
            String url = String.format("%sweather?q=%s&appid=%s&units=metric", apiUrl, location, apiKey);
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return "Location not found, please update your location to see the weather";
        }
    }

    public String getForecast(String location) {
        try {
            String url = String.format("%sforecast?q=%s&appid=%s&units=metric", apiUrl, location, apiKey);
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return "Location not found, please update your location to see the weather";
        }
    }

}