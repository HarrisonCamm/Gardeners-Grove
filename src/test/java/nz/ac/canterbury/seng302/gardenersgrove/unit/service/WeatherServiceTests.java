package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.ForecastResponse;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class WeatherServiceTests {
    private WeatherService weatherService;
    private static String validCurWeatherJsonString;
    private static String validForecastWeatherJsonString;
    private static String validHistoricForcastWeatherJsonString;
    private static String validHistoricForcastWeatherNoRainJsonString;
    private static String invalidJsonNullCityResponse;

    private static final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);




    @BeforeAll
    static public void  jsonSetup() throws Exception {
        //read json example file
        validCurWeatherJsonString = Files.readString(Paths.get("src/test/resources/json/validCurrentWeather.json"));
        validForecastWeatherJsonString = Files.readString(Paths.get("src/test/resources/json/validForecast.json"));
        invalidJsonNullCityResponse = Files.readString(Paths.get("src/test/resources/json/invalidCityForcastWeatherResponse.json"));
        validHistoricForcastWeatherJsonString = Files.readString(Paths.get("src/test/resources/json/validHistoricForcastWeather.json"));
        validHistoricForcastWeatherNoRainJsonString = Files.readString(Paths.get("src/test/resources/json/validHistoricWeatherForcastNoRain.json"));
    }

    @BeforeEach
    public void setUp() {
        //mock CountryCodeService for separation of concerns
        CountryCodeService countryCodeService = Mockito.mock(CountryCodeService.class);
        Mockito.when(countryCodeService.getCountryCode(any(String.class))).thenReturn("");

        weatherService = new WeatherService(countryCodeService, restTemplate);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullAndEmptySource
    public void NullEmptyCity_GetCurrentWeather_ReturnsNull(String city) {
        WeatherResponse weatherServiceResponse = weatherService.getCurrentWeather(city, "New Zealand");
        Assertions.assertNull(weatherServiceResponse);
    }

    @ParameterizedTest()
    @ValueSource(strings = {"chch", "Welly", "Akl", "'", "otautahi"})
    public void InvalidCity_GetCurrentWeather_ReturnsNull(String city) {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(invalidJsonNullCityResponse);
        WeatherResponse weatherServiceResponse = weatherService.getCurrentWeather(city, "New Zealand");
        Assertions.assertNull(weatherServiceResponse);
    }


    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullAndEmptySource
    public void NullEmptyCountry_GetCurrentWeather_ReturnsWeatherResponse(String country) {
        WeatherResponse weatherServiceResponse = weatherService.getCurrentWeather("Auckland", country);
        Assertions.assertNotNull(weatherServiceResponse);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullAndEmptySource
    public void NullEmptyCity_HasRained_ReturnsNull(String city) {
        Boolean hasRained = weatherService.hasRained(city, "New Zealand");
        Assertions.assertNull(hasRained);
    }

    @Test
    public void ValidCity_NoRainDetected_ReturnsFalse() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(validHistoricForcastWeatherNoRainJsonString);

        Boolean hasRained = weatherService.hasRained("Auckland", "New Zealand");
        Assertions.assertFalse(hasRained);
    }

    @Test
    public void ValidCity_RainDetected_ReturnsTrie() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(validHistoricForcastWeatherJsonString);

        Boolean hasRained = weatherService.hasRained("Auckland", "New Zealand");
        Assertions.assertTrue(hasRained);
    }

    @Test
    public void InvalidCity_HasRained_ReturnsNull() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(invalidJsonNullCityResponse);

        Boolean hasRained = weatherService.hasRained("InvalidCity", "New Zealand");
        Assertions.assertNull(hasRained);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullAndEmptySource
    public void NullEmptyCountry_HasRained_ReturnsBoolean(String country) {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(validHistoricForcastWeatherJsonString);

        Boolean hasRained = weatherService.hasRained("Auckland", country);
        Assertions.assertTrue(hasRained); // Assume true based on response
    }

    @Test
    public void ApiCallFailure_HasRained_ReturnsNull() {
        Mockito.when(restTemplate.getForObject(any(String.class), any()))
                .thenThrow(new RuntimeException("API call failed"));

        Boolean hasRained = weatherService.hasRained("Auckland", "New Zealand");
        Assertions.assertNull(hasRained);
    }

    @Test
    public void ValidJson_GetCurrentWeather_ReturnsWeatherResponse() {
        //Set which json to use
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(validCurWeatherJsonString);

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

    @Test
    public void ValidJson_GetForecast_ReturnsWeatherResponse() {
        //Set which json to use
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(validForecastWeatherJsonString);


        List<String> expectedDaysOfWeek = Arrays.asList("Friday", "Saturday", "Sunday", "Monday", "Tuesday");
        List<String> expectedDates = Arrays.asList("02/08/2024", "03/08/2024", "04/08/2024", "05/08/2024", "06/08/2024");
        List<Double> expectedTemperatures = Arrays.asList(284.36, 284.04, 285.62, 285.3, 286.25);
        List<String> expectedMainWeather = Arrays.asList("Rain", "Rain", "Rain", "Rain", "Rain");
        List<Integer> expectedHumiditys = Arrays.asList(76, 66, 69, 59, 71);
        List<String> expectedIcons = Arrays.asList("10d", "10d", "10d", "10d", "10d");


        ForecastResponse forecastResponse = weatherService.getForecastWeather("Auckland", "New Zealand");
        Assertions.assertNotNull(forecastResponse);

        List<WeatherResponse> forecasts = Arrays.asList(forecastResponse.getWeatherResponses());
        for (int i = 0; i < forecasts.size(); i++) {
            Assertions.assertEquals(expectedDaysOfWeek.get(i), forecasts.get(i).getDayOfWeek());
            Assertions.assertEquals(expectedDates.get(i), forecasts.get(i).getDate());
            Assertions.assertEquals(expectedMainWeather.get(i), forecasts.get(i).getDescription());
            Assertions.assertEquals(expectedTemperatures.get(i), forecasts.get(i).getTemperature());
            Assertions.assertEquals(expectedHumiditys.get(i), forecasts.get(i).getHumidity());
            Assertions.assertEquals(expectedIcons.get(i), forecasts.get(i).getWeatherIcon());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullAndEmptySource
    public void NullEmptyCity_GetForecast_ReturnsNull(String city) {
        ForecastResponse weatherServiceResponse = weatherService.getForecastWeather(city, "New Zealand");
        Assertions.assertNull(weatherServiceResponse);
    }


    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullAndEmptySource
    public void NullEmptyCountry_GetForecast_ReturnsForecastResponse(String country) {
        ForecastResponse weatherServiceResponse = weatherService.getForecastWeather("Auckland", country);
        Assertions.assertNotNull(weatherServiceResponse);
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
