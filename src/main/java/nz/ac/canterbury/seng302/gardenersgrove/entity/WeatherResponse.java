package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.text.SimpleDateFormat;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {

    @JsonDeserialize
    @JsonProperty("weather")
    private WeatherInfo[] weatherInfo;

    @JsonDeserialize
    @JsonProperty("main")
    private MainInfo mainInfo;

    @JsonDeserialize
    @JsonProperty("dt")
    private Long date;

    @JsonDeserialize
    @JsonProperty("id")
    private int id;

    @JsonDeserialize
    @JsonProperty("cod")
    private int cod;

    public double getTemperature() {
        return mainInfo != null ? mainInfo.temperature : 0.0;
    }

    public int getHumidity() {
        return mainInfo != null ? mainInfo.humidity : 0;
    }

    public String getWeatherDescription() {
        return weatherInfo != null && weatherInfo.length > 0 ? weatherInfo[0].description : "";
    }

    public String getWeatherIcon() {
        return weatherInfo != null && weatherInfo.length > 0 ? weatherInfo[0].icon : "";
    }

    public String getDate() {
        SimpleDateFormat dateDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        return dateDateFormat.format(date * 1000);
    }

    public String getDayOfWeek() {
        SimpleDateFormat dayDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        return dayDateFormat.format(date * 1000);
    }

    public int getId() {
        return id;
    }

    public int getCod() {
        return cod;
    }

    public String getDescription() {
        return weatherInfo != null && weatherInfo.length > 0 ? weatherInfo[0].mainDescription : "";
    }

    public String getIcon() {
        return weatherInfo != null && weatherInfo.length > 0 ? weatherInfo[0].icon : "";
    }

    // Inner classes for nested objects
    @JsonDeserialize
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainInfo {

        @JsonProperty("temp")
        public double temperature;

        @JsonProperty("humidity")
        public int humidity;

        public MainInfo() {
            // no-args jackson constructor
        }
    }

    @JsonDeserialize
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherInfo {

        @JsonProperty("main")
        public String mainDescription;

        @JsonProperty("description")
        public String description;

        @JsonProperty("icon")
        public String icon;

        public WeatherInfo() {
            // no-args jackson constructor
        }
    }
}
