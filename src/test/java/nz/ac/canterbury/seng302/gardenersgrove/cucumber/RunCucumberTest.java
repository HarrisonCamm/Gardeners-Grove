package nz.ac.canterbury.seng302.gardenersgrove.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import jakarta.servlet.annotation.MultipartConfig;
import nz.ac.canterbury.seng302.gardenersgrove.GardenersGroveApplication;
import nz.ac.canterbury.seng302.gardenersgrove.entity.ForecastResponse;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantData;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantGuesserList;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WeatherResponse;
import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantGuesserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.WeatherService;
import org.junit.platform.suite.api.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper; // Weather service one
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static java.lang.Math.min;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
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
@MockBean(WebSocketStompClient.class)
@MockBean(StompSession.class)
@MockBean(StompFrameHandler.class)
@MockBean(PlantGuesserService.class)

public class RunCucumberTest {

    private static WeatherResponse mockedValidCurrentWeather;
    private static ForecastResponse mockedValidForecast;
    private static WeatherResponse mockedNullCityWeather;
    private static ForecastResponse mockedNullCityForecast;

    private static PlantData plant;
    private static List<String> fourOptions;
    private static String Rained;
    private static String NotRained;
    private static String Raining;
    private static String NotRaining;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    final Message[] receivedMessage = new Message[1]; // Define receivedMessage here

    // Loads static variables when the class is first loaded
    static {
        try {
            // Read and parse JSON files
            // SUGGESTION
            String currentWeatherJsonString = Files.readString(Paths.get("src/test/resources/json/validCurrentWeather.json"));
            String forecastWeatherJsonString = Files.readString(Paths.get("src/test/resources/json/validForecast.json"));

            String jsonNullCityResponse = Files.readString(Paths.get("src/test/resources/json/invalidCityForcastWeatherResponse.json"));

            String plantPageJsonString = Files.readString(Paths.get("src/test/resources/json/getPlantsResponse.json"));
            String plantFamilyPageJsonString = Files.readString(Paths.get("src/test/resources/json/getPlantFamilyResponse.json"));

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

            //To mock plant api
            PlantGuesserList mockedPlantPage = objectMapper.readValue(plantPageJsonString, PlantGuesserList.class);
            List<PlantData> plantList = new ArrayList<>(Arrays.stream(mockedPlantPage.getPlantGuesserList()).toList());
            plant = plantList.get(0);


            PlantGuesserList mockedPlantFamilyPage = objectMapper.readValue(plantFamilyPageJsonString, PlantGuesserList.class);
            PlantData[] plantFamilyMembers = Arrays.stream(mockedPlantFamilyPage.getPlantGuesserList()).toList()
                    .stream()
                    .filter(plant_i -> !Objects.equals(plant_i.common_name, plant.common_name))
                    .toArray(PlantData[]::new);
            List<String> multichoicePlantNames = new ArrayList<>(Arrays.stream(plantFamilyMembers).toList()
                    .stream()
                    .map(PlantData::getCommonAndScientificName)
                    .toList());

            List<String> correctOption = Collections.singletonList(plant.getCommonAndScientificName());
            fourOptions = Stream.concat(multichoicePlantNames.subList(0,3).stream(), correctOption.stream())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public RunCucumberTest(ModerationService moderationService,
                           WeatherService weatherService,
                           StompSession stompSession,
                           StompFrameHandler stompFrameHandler,
                           PlantGuesserService plantGuesserService) {

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
        when(moderationService.moderateText(null)).thenReturn("null");
        when(moderationService.isContentAppropriate(null)).thenReturn(true);

        when(moderationService.moderateText(eq("NotEvaluated"))).thenReturn("evaluation_error");

        // Mock unsuccessful moderation (profanity detected)
        when(moderationService.moderateText(eq("InappropriateTag"))).thenReturn("[{\"term\":\"InappropriateTerm\"}]");


        // Weather Service API mocks
        // Mock successful current weather service response
        when(weatherService.getCurrentWeather(anyString(), anyString())).thenReturn(mockedValidCurrentWeather);
        // Mock successful weather forecast service response
        when(weatherService.getForecastWeather(anyString(), anyString())).thenReturn(mockedValidForecast);

        // Mock unsuccessful location current weather service response
        when(weatherService.getCurrentWeather(eq("InvalidCity"), anyString())).thenReturn(null);
        // Mock unsuccessful location weather forecast service response
        when(weatherService.getForecastWeather(eq("InvalidCity"), anyString())).thenReturn(null);

        // Mock successful has rained in the last 2 days
        when(weatherService.hasRained(eq(Rained), anyString())).thenReturn(true);
        // Mock unsuccessful has not rained in the last 2 days
        when(weatherService.hasRained(eq(NotRained), anyString())).thenReturn(false);

        // Mock successful is currently raining
        when(weatherService.isRaining(eq(Raining), anyString())).thenReturn(true);
        // Mock unsuccessful is not currently raining
        when(weatherService.isRaining(eq(NotRaining), anyString())).thenReturn(false);

        when(moderationService.isBusy()).thenReturn(false);
        when(moderationService.isContentAppropriate("DelayedEvaluated")).thenReturn(true);
        when(moderationService.isContentAppropriate("InappropriateEvaluated")).thenReturn(false);


        when(plantGuesserService.getPlant()).thenReturn(plant);
        when(plantGuesserService.getMultichoicePlantNames(plant.family, plant.common_name, plant.getCommonAndScientificName())).thenReturn(fourOptions);

        // WebSocketStompClient mocks
        CompletableFuture<StompSession> sessionFuture = new CompletableFuture<>();
        sessionFuture.complete(stompSession);

        stompFrameHandler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedMessage[0] = (Message) payload;
            }
        };


        when(stompSession.subscribe(anyString(), any(StompFrameHandler.class))).thenAnswer(invocation -> {
            StompFrameHandler handler = invocation.getArgument(1);
            StompHeaders headers = new StompHeaders();

            // Simulate message delivery by calling the handler's handleFrame method
            handler.handleFrame(headers, new Message("sarah@email.com", "inaya@email.com", "Hello", new Date()));

            return null;
        });

        // Mock send method to simulate the sending of a message and calling handleFrame
        StompFrameHandler finalStompFrameHandler = stompFrameHandler;

        when(stompSession.send(anyString(), any())).thenAnswer(invocation -> {
            Message sentMessage = invocation.getArgument(1);

            // Simulate the message being processed and received by invoking handleFrame directly
            StompHeaders headers = new StompHeaders();
            finalStompFrameHandler.handleFrame(headers, sentMessage);

            return null;
        });
    }

    private WeatherResponse getMockedValidCurrentWeather() {
        // Create and return mocked WeatherResponse object
        return new WeatherResponse();  // Populate with necessary fields
    }

    private ForecastResponse getMockedValidForecast() {
        // Create and return mocked ForecastResponse object
        return new ForecastResponse();  // Populate with necessary fields
    }

    public Message getReceivedMessage() {
        return receivedMessage[0];
    }
}
