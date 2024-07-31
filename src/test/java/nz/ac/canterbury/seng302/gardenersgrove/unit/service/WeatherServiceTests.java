package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.WeatherResponse;
import nz.ac.canterbury.seng302.gardenersgrove.service.CountryCodeService;
import nz.ac.canterbury.seng302.gardenersgrove.service.WeatherService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;

public class WeatherServiceTests {
    private WeatherService weatherService;
    private static String jsonString;

    @BeforeAll
    static public void  jsonSetup() throws Exception {
        //read json example file
        jsonString = Files.readString(Paths.get("src/test/resources/json/blueSkyWeather.json"));
    }

    @BeforeEach
    public void setUp() {
        //mock CountryCodeService for separation of concerns
        CountryCodeService countryCodeService = Mockito.mock(CountryCodeService.class);
        Mockito.when(countryCodeService.getCountryCode(any(String.class))).thenReturn("");

        RestTemplate restTemplate;
        restTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(jsonString);
        weatherService = new WeatherService(countryCodeService, restTemplate);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullAndEmptySource
    public void NullEmptyCity_GetCurrentWeather_ReturnsNull(String city) {
        WeatherResponse weatherServiceResponse = weatherService.getCurrentWeather(city, "New Zealand");
        Assertions.assertNull(weatherServiceResponse);
    }

    @ParameterizedTest
    @ValueSource(strings = {"chch", "Welly", "Akl", "'", "otautahi"})
    public void InvalidCity_GetCurrentWeather_ReturnsNull(String city) {
        WeatherResponse weatherServiceResponse = weatherService.getCurrentWeather(city, "New Zealand");
        Assertions.assertNull(weatherServiceResponse);
    }


    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullAndEmptySource    public void NullEmptyCountry_GetCurrentWeather_ReturnsWeatherResponse(String country) {
        WeatherResponse weatherServiceResponse = weatherService.getCurrentWeather("Auckland", country);
        Assertions.assertNotNull(weatherServiceResponse);
    }

    @Test
    public void ValidJson_GetCurrentWeather_ReturnsWeatherResponse() {
        //Expected values from json
        String expectedDayOfWeek = "Saturday";
        String expectedDate = "27/07/2024";
        String expectedDescription = "Clouds";
        Double expectedTemperature = 281.54;
        int expectedHumidity = 73;
        String expectedIcon = "04d";

        WeatherResponse weatherServiceResponse = weatherService.getCurrentWeather("Auckland", "New Zealand");
        Assertions.assertNotNull(weatherServiceResponse);

        Assertions.assertEquals(expectedDayOfWeek, weatherServiceResponse.getDayOfWeek());
        Assertions.assertEquals(expectedDate, weatherServiceResponse.getDate());
        Assertions.assertEquals(expectedDescription, weatherServiceResponse.getDescription());
        Assertions.assertEquals(expectedTemperature, weatherServiceResponse.getTemperature());
        Assertions.assertEquals(expectedHumidity, weatherServiceResponse.getHumidity());
        Assertions.assertEquals(expectedIcon, weatherServiceResponse.getWeatherIcon());
    }

    //Will return to this when I can get help to get the right properties profile to activate
//    @Nested
//    @ActiveProfiles("test")
//    class WeatherServiceTestsTestProperties {
//        //really just testing that it won't 💥 if there's no api key
//        @Test
//        public void NoApiKey_GetCurrentWeather_ReturnsNull() {
//            WeatherResponse weatherServiceResponse = weatherService.getCurrentWeather("", "");
//            Assertions.assertNull(weatherServiceResponse);
//        }
//    }
}
