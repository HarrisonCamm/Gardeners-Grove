package nz.ac.canterbury.seng302.gardenersgrove.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import jakarta.servlet.annotation.MultipartConfig;
import nz.ac.canterbury.seng302.gardenersgrove.GardenersGroveApplication;
import nz.ac.canterbury.seng302.gardenersgrove.entity.ForecastResponse;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WeatherResponse;
import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
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
import java.util.Date;
import java.util.concurrent.CompletableFuture;

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

public class RunCucumberTest {
    private static WeatherResponse mockedValidCurrentWeather;
    private static ForecastResponse mockedValidForecast;
    private static WeatherResponse mockedNullCityWeather;
    private static ForecastResponse mockedNullCityForecast;
    private static String Rained;
    private static String NotRained;
    private static String Raining;
    private static String NotRaining;

    final Message[] receivedMessage = new Message[1]; // Define receivedMessage here


    private static final ObjectMapper objectMapper = new ObjectMapper();



    // Loads static variables when the class is first loaded
    static {
        try {
            // Read and parse JSON files
            // SUGGESTION
            String currentWeatherJsonString = Files.readString(Paths.get("src/test/resources/json/validCurrentWeather.json"));
            String forecastWeatherJsonString = Files.readString(Paths.get("src/test/resources/json/validForecast.json"));

            String jsonNullCityResponse = Files.readString(Paths.get("src/test/resources/json/invalidCityForcastWeatherResponse.json"));

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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public RunCucumberTest(ModerationService moderationService,
                           WeatherService weatherService,
                           StompSession stompSession,
                           StompFrameHandler stompFrameHandler) {

        // ModerationService mocks
        when(moderationService.moderateText(anyString())).thenReturn("null");
        when(moderationService.moderateText(eq("NotEvaluated"))).thenReturn("evaluation_error");
        when(moderationService.moderateText(eq("InappropriateTag"))).thenReturn("[{\"term\":\"InappropriateTerm\"}]");
        when(moderationService.isBusy()).thenReturn(false);
        when(moderationService.isContentAppropriate("DelayedEvaluated")).thenReturn(true);
        when(moderationService.isContentAppropriate("InappropriateEvaluated")).thenReturn(false);

        // WeatherService mocks
        WeatherResponse mockedValidCurrentWeather = getMockedValidCurrentWeather();
        ForecastResponse mockedValidForecast = getMockedValidForecast();

        when(weatherService.getCurrentWeather(anyString(), anyString())).thenReturn(mockedValidCurrentWeather);
        when(weatherService.getForecastWeather(anyString(), anyString())).thenReturn(mockedValidForecast);



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
