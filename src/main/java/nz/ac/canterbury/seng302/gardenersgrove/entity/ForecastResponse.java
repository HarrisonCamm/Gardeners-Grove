package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastResponse {

    @JsonDeserialize
    @JsonProperty("list")
    private WeatherResponse[] weatherResponses;

    public ForecastResponse() {
        // no-args jackson constructor
    }

}
