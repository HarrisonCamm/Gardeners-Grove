package nz.ac.canterbury.seng302.gardenersgrove.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import nz.ac.canterbury.seng302.gardenersgrove.GardenersGroveApplication;
import nz.ac.canterbury.seng302.gardenersgrove.entity.ForecastResponse;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WeatherResponse;
import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.WeatherService;
import org.junit.platform.suite.api.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper; // Weather service one

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("nz/ac/canterbury/seng302/gardenersgrove/cucumber")
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "nz.ac.canterbury.seng302.gardenersgrove.cucumber"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-report/cucumber.html"),
        @ConfigurationParameter(key = Constants.PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")
})
@ContextConfiguration(classes = GardenersGroveApplication.class)
@CucumberContextConfiguration
@SpringBootTest
@ActiveProfiles("cucumber")
@AutoConfigureMockMvc

// Permanent api mocks
@MockBean(ModerationService.class)
@MockBean(WeatherService.class)

public class RunCucumberTest {
    private static WeatherResponse mockedValidCurrentWeather;
    private static ForecastResponse mockedValidForecast;
    private static WeatherResponse mockedNullCityWeather;
    private static ForecastResponse mockedNullCityForecast;
    private static String Rained;
    private static String NotRained;
    private static String Raining;
    private static String NotRaining;

    private static final ObjectMapper objectMapper = new ObjectMapper();



    // Loads static variables when the class is first loaded
    static {
        try {
            // Read and parse JSON files
            // SUGGESTION
            String currentWeatherJsonString = Files.readString(Paths.get("src/test/resources/json/validCurrentWeather.json"));
            String forecastWeatherJsonString = Files.readString(Paths.get("src/test/resources/json/validForecast.json"));

            String jsonNullCityResponse = Files.readString(Paths.get("src/test/resources/json/invalidCityForcastWeatherResponse.json"));

//            String historicWeatherJsonString = Files.readString(Paths.get("src/test/resources/json/validHistoricForcastWeather.json"));
//            String historicWeatherNoRainJsonString = Files.readString(Paths.get("src/test/resources/json/validHistoricWeatherForcastNoRain.json"));
//
//            String currentWeatherRainJsonString = Files.readString(Paths.get("src/test/resources/json/validCurrentWeatherRain.json"));



            Rained = "Rained";
            NotRained = "NotRained";
            Raining = "Raining";
            NotRaining = "NotRaining";


            // Valid weather and forecast
            mockedValidCurrentWeather = objectMapper.readValue(currentWeatherJsonString, WeatherResponse.class);
            mockedValidForecast = objectMapper.readValue(forecastWeatherJsonString, ForecastResponse.class);

            // Invalid weather and forecast
            mockedNullCityWeather = objectMapper.readValue(jsonNullCityResponse, WeatherResponse.class);
            mockedNullCityForecast = objectMapper.readValue(jsonNullCityResponse, ForecastResponse.class);

//            // Has rained in the last two days
//            mockedValidHistoricForcastWeatherHasRain = objectMapper.readValue(historicWeatherJsonString, Boolean.class);
//            // Has not rained in the last two days
//            mockedValidHistoricForcastWeatherNoRain = objectMapper.readValue(historicWeatherNoRainJsonString, Boolean.class);
//
//            // Is currently raining
//            mockedValidCurrentWeatherIsRaining = objectMapper.readValue(currentWeatherRainJsonString, Boolean.class);
//            // Is not currently raining
//            mockedValidCurrentWeatherNotRaining = objectMapper.readValue(currentWeatherJsonString, Boolean.class);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public RunCucumberTest(ModerationService moderationService, WeatherService weatherService)  {
        /*
         This constructor is run before every FEATURE, use it to set up mocks with their default behaviour.
         While the behaviour of the mocks can be adapted per test (see MockConfigurationSteps), creating the mocks
         initially should be done in this class, and their default behaviour configured here (see @MockBean above).

         Additionally, you can do other setup here that should be done the same for all tests, such as adding default
         users. If you want to get rid of any sample data in some cases, you can always write a Cucumber step to delete
         it, e.g., `Given no users already exist in the database` if you wanted to make sure there were no existing
         users for some particular feature.
        */

        // Moderation Service API mocks
        // Mock successful moderation
        when(moderationService.moderateText(anyString())).thenReturn("null");
        // Mock unsuccessful moderation (profanity detected)
        when(moderationService.moderateText(eq("InappropriateTag"))).thenReturn("[{\"term\":\"InappropriateTerm\"}]");


        // Weather Service API mocks
        // Mock successful current weather service response
        when(weatherService.getCurrentWeather(anyString(), anyString())).thenReturn(mockedValidCurrentWeather);
        // Mock successful weather forecast service response
        when(weatherService.getForecastWeather(anyString(), anyString())).thenReturn(mockedValidForecast);

        // Mock unsuccessful location current weather service response
        when(weatherService.getCurrentWeather(eq("InvalidCity"), anyString())).thenReturn(mockedNullCityWeather);
        // Mock unsuccessful location weather forecast service response
        when(weatherService.getForecastWeather(eq("InvalidCity"), anyString())).thenReturn(mockedNullCityForecast);

        // Mock successful has rained in the last 2 days
        when(weatherService.hasRained(eq(Rained), anyString())).thenReturn(true);
        // Mock unsuccessful has not rained in the last 2 days
        when(weatherService.hasRained(eq(NotRained), anyString())).thenReturn(false);

        // Mock successful is currently raining
        when(weatherService.isRaining(Raining, anyString())).thenReturn(true);
        // Mock unsuccessful is not currently raining
        when(weatherService.isRaining(NotRaining, anyString())).thenReturn(false);
    }
}
