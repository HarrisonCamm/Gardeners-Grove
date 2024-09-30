package nz.ac.canterbury.seng302.gardenersgrove.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
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
    private static List<PlantData> plantList;
    private static PlantData plant1;
    private static PlantData plant2;
    private static List<String> fourOptions1;
    private static List<String> fourOptions2;
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


            // Parse the combined JSON
            JsonNode combinedData = objectMapper.readTree(plantFamilyPageJsonString);

            // Extract "pine_family" and "brassicaceae_family" as separate strings
            String plantFamily1PageJsonString = combinedData.get("plant1_family").toString();
            String plantFamily2PageJsonString = combinedData.get("plant2_family").toString();


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

            //To mock plant api
            PlantGuesserList mockedPlantPage = objectMapper.readValue(plantPageJsonString, PlantGuesserList.class);
            plantList = new ArrayList<>(Arrays.stream(mockedPlantPage.getPlantGuesserList()).toList());
            plant1 = plantList.get(0);
            plant2 = plantList.get(1);


            PlantGuesserList mockedPlantFamilyPage1 = objectMapper.readValue(plantFamily1PageJsonString, PlantGuesserList.class);
            PlantData[] plantFamilyMembers1 = Arrays.stream(mockedPlantFamilyPage1.getPlantGuesserList()).toList()
                    .stream()
                    .filter(eachPlant -> !Objects.equals(eachPlant.common_name, plant1.common_name))
                    .toArray(PlantData[]::new);
            List<String> multichoicePlantNames1 = new ArrayList<>(Arrays.stream(plantFamilyMembers1).toList()
                    .stream()
                    .map(PlantData::getCommonAndScientificName)
                    .toList());

            List<String> correctOption1 = Collections.singletonList(plant1.getCommonAndScientificName());
            fourOptions1 = Stream.concat(multichoicePlantNames1.subList(0,3).stream(), correctOption1.stream())
                    .collect(Collectors.toList());


            PlantGuesserList mockedPlantFamilyPage2 = objectMapper.readValue(plantFamily2PageJsonString, PlantGuesserList.class);
            PlantData[] plantFamilyMembers2 = Arrays.stream(mockedPlantFamilyPage2.getPlantGuesserList()).toList()
                    .stream()
                    .filter(eachPlant -> !Objects.equals(eachPlant.common_name, plant2.common_name))
                    .toArray(PlantData[]::new);
            List<String> multichoicePlantNames2 = new ArrayList<>(Arrays.stream(plantFamilyMembers2).toList()
                    .stream()
                    .map(PlantData::getCommonAndScientificName)
                    .toList());

            List<String> correctOption2 = Collections.singletonList(plant2.getCommonAndScientificName());
            fourOptions2 = Stream.concat(multichoicePlantNames2.subList(0,3).stream(), correctOption2.stream())
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

        when(plantGuesserService.getPlantRound()).thenReturn(plantList);

        when(plantGuesserService.getMultichoicePlantNames(plant1.family, plant1.common_name, plant1.getCommonAndScientificName()))
                .thenReturn(fourOptions1);

        when(plantGuesserService.getMultichoicePlantNames(plant2.family, plant2.common_name, plant2.getCommonAndScientificName()))
                .thenReturn(fourOptions2);


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
