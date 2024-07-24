package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.text.SimpleDateFormat;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {

    @JsonDeserialize
    @JsonProperty("coord")
    private Coord coord;

    @JsonDeserialize
    @JsonProperty("weather")
    private WeatherInfo[] weatherInfo;

    @JsonDeserialize
    @JsonProperty("base")
    private String base;

    @JsonDeserialize
    @JsonProperty("main")
    private MainInfo mainInfo;

    @JsonDeserialize
    @JsonProperty("visibility")
    private int visibility;

    @JsonDeserialize
    @JsonProperty("wind")
    private Wind wind;

    @JsonDeserialize
    @JsonProperty("clouds")
    private Clouds clouds;

    @JsonDeserialize
    @JsonProperty("dt")
    private Long date;

    @JsonDeserialize
    @JsonProperty("sys")
    private Sys sys;

    @JsonDeserialize
    @JsonProperty("timezone")
    private int timezone;

    @JsonDeserialize
    @JsonProperty("id")
    private int id;

    @JsonDeserialize
    @JsonProperty("name")
    private String name;

    @JsonDeserialize
    @JsonProperty("cod")
    private int cod;

    // Getters for parent class attributes

    public Coord getCoord() {
        return coord;
    }

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

    public String getBase() {
        return base;
    }

    public int getVisibility() {
        return visibility;
    }

    public Wind getWind() {
        return wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public Sys getSys() {
        return sys;
    }

    public int getTimezone() {
        return timezone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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
    public static class Coord {

        @JsonProperty("lon")
        public double lon;

        @JsonProperty("lat")
        public double lat;

        public Coord() {
            // no-args jackson constructor
        }
    }

    @JsonDeserialize
    public static class MainInfo {

        @JsonProperty("temp")
        public double temperature;

        @JsonProperty("feels_like")
        public double feelsLike;

        @JsonProperty("temp_min")
        public double tempMin;

        @JsonProperty("temp_max")
        public double tempMax;

        @JsonProperty("pressure")
        public int pressure;

        @JsonProperty("humidity")
        public int humidity;

        @JsonProperty("sea_level")
        public int seaLevel;

        @JsonProperty("grnd_level")
        public int grndLevel;

        public MainInfo() {
            // no-args jackson constructor
        }
    }

    @JsonDeserialize
    public static class WeatherInfo {

        @JsonProperty("id")
        public int id;

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

    @JsonDeserialize
    public static class Wind {

        @JsonProperty("speed")
        public double speed;

        @JsonProperty("deg")
        public int deg;

        @JsonProperty("gust")
        public double gust;

        public Wind() {
            // no-args jackson constructor
        }
    }

    @JsonDeserialize
    public static class Clouds {

        @JsonProperty("all")
        public int all;

        public Clouds() {
            // no-args jackson constructor
        }
    }

    @JsonDeserialize
    public static class Sys {

        @JsonProperty("type")
        public int type;

        @JsonProperty("id")
        public int id;

        @JsonProperty("country")
        public String country;

        @JsonProperty("sunrise")
        public Long sunrise;

        @JsonProperty("sunset")
        public Long sunset;

        public Sys() {
            // no-args jackson constructor
        }
    }
}
