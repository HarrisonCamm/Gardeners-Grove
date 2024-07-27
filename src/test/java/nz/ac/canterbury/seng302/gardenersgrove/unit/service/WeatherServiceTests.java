package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.WeatherResponse;
import nz.ac.canterbury.seng302.gardenersgrove.service.CountryCodeService;
import nz.ac.canterbury.seng302.gardenersgrove.service.WeatherService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;

public class WeatherServiceTests {
    private WeatherService weatherService;
    private static String jsonString;

    //Expected values from json
    private final String expectedDayOfWeek = "Saturday";
    private final String expectedDate = "27/07/2024";
    private final String expectedDescription = "Clouds";
    private final Double expectedTemperature = 281.54;
    private final int expectedHumidity = 73;
    private final String expectedIcon = "04d";


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

    @Test
    public void EmptyCity_GetCurrentWeather_ReturnsNull() {
        WeatherResponse weatherServiceResponse = weatherService.getCurrentWeather("", "New Zealand");
        Assertions.assertNull(weatherServiceResponse);
    }

    @Test
    public void NullCity_GetCurrentWeather_ReturnsNull() {
    WeatherResponse weatherServiceResponse = weatherService.getCurrentWeather(null, "New Zealand");
        Assertions.assertNull(weatherServiceResponse);
    }

    @Test
    public void EmptyCountry_GetCurrentWeather_ReturnsWeatherResponse() {
        WeatherResponse weatherServiceResponse = weatherService.getCurrentWeather("Auckland", "");
        Assertions.assertNotNull(weatherServiceResponse);
    }

    @Test
    public void NullCountry_GetCurrentWeather_ReturnsWeatherResponse() {
        WeatherResponse weatherServiceResponse = weatherService.getCurrentWeather("Auckland", null);
        Assertions.assertNotNull(weatherServiceResponse);
    }

    @Test
    public void ValidJson_GetCurrentWeather_ReturnsWeatherResponse() {
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
////    @ActiveProfiles("test")
//    class WeatherServiceTestsTestProperties {
//        //really just testing that it won't 💥 if there's no api key
//        @Test
//        public void NoApiKey_GetCurrentWeather_ReturnsNull() {
//            WeatherResponse weatherServiceResponse = weatherService.getCurrentWeather("", "");
//            Assertions.assertNull(weatherServiceResponse);
//        }
//    }


}
