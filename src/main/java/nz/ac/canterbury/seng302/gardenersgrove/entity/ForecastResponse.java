package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastResponse {

    @JsonDeserialize
    @JsonProperty("list")
    private WeatherResponse[] weatherResponses;

    public ForecastResponse() {
        // no-args jackson constructor
    }

    /**
     * Returns the WeatherResponse array starting from the second item (index 1) onwards.
     * Because the first item is the current days weather forecast != current weather.
     * @return a new WeatherResponse array starting from the second item.
     */
    public WeatherResponse[] getWeatherResponses() {
        if (weatherResponses == null || weatherResponses.length <= 1) {
            return new WeatherResponse[0]; // Return an empty array if there's no second item
        }
        return Arrays.copyOfRange(weatherResponses, 1, weatherResponses.length);
    }}
