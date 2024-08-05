package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.sl.In;
import nz.ac.canterbury.seng302.gardenersgrove.entity.ForecastResponse;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WeatherResponse;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class WeatherMonitoringSteps {

    private static Garden ownedGarden;
    private static ResultActions resultActions;

    @Autowired
    private GardenService gardenService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private String validCurWeatherJsonString;

    private Long todayDate;

    private String description;

    private double temperature;
    private double humidity;

    @Before
    public void setup() throws IOException {
        validCurWeatherJsonString = Files.readString(Paths.get("src/test/resources/json/validCurrentWeather.json"));
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Parse the "dt" field from the JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(validCurWeatherJsonString);

        todayDate = jsonNode.get("dt").asLong();
        description = jsonNode.get("weather").get(0).get("main").asText();
        temperature = jsonNode.get("main").get("temp").asDouble();
        humidity = jsonNode.get("main").get("humidity").asDouble();
    }

    @Given("I am a garden owner with in {string} in {string}")
    public void iAmAGardenOwnerWithInIn(String city, String country) throws Exception {

        resultActions = mockMvc.perform(post("/create-garden")
                .param("name", "Kaia's Garden")
                .param("location.streetAddress", "")
                .param("location.suburb", "")
                .param("location.city", city)
                .param("location.postcode", "")
                .param("location.country", country)
                .param("size", "10")
                .with(csrf())); // Add CSRF token

        ArrayList<Garden> gardens = (ArrayList<Garden>) gardenService.getOwnedGardens(userService.getAuthenticatedUser().getUserId());
        ownedGarden = gardens.get(gardens.size() - 1);
        assertNotNull(gardens);
    }

    @And("I am on the garden details page for a garden I own to check the weather")
    public void i_am_on_the_garden_details_page_for_a_garden_i_own() throws Exception {
        resultActions = mockMvc.perform(get("/view-garden")
                        .param("gardenID", ownedGarden.getId().toString())
                        .with(csrf())) // Add CSRF token
                .andExpect(status().isOk());
    }

    @When("I look at the weather section")
    public void iLookAtTheWeatherSection() {
        assertNotNull(Objects.requireNonNull(resultActions.andReturn().getModelAndView()).getModel().get("forecastResponse"));
    }

    @Then("the current day of the week is shown")
    public void theCurrentDayOfTheWeekIsShown() {
        ForecastResponse forecastResponse = (ForecastResponse) Objects.requireNonNull(resultActions.andReturn().getModelAndView()).getModel().get("forecastResponse");
        assertNotNull(forecastResponse);

        Instant instant = Instant.ofEpochSecond(todayDate);
        LocalDate dateFromJson = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        DayOfWeek dayOfWeekFromJson = dateFromJson.getDayOfWeek();

        Long timestampResponse = forecastResponse.getAllWeatherResponses()[0].getRawDate();
        Instant instantResponse = Instant.ofEpochSecond(timestampResponse);
        LocalDate dateFromResponse = instantResponse.atZone(ZoneId.systemDefault()).toLocalDate();
        DayOfWeek dayOfWeekFromResponse = dateFromResponse.getDayOfWeek();

        // Toby got the response on Saturday :)
        assertEquals(dayOfWeekFromJson, dayOfWeekFromResponse);
    }

    @And("the current date is shown")
    public void theCurrentDateIsShown() {
        ForecastResponse forecastResponse = (ForecastResponse) Objects.requireNonNull(resultActions.andReturn().getModelAndView()).getModel().get("forecastResponse");

        assertEquals(todayDate, forecastResponse.getAllWeatherResponses()[0].getRawDate());
    }

    @And("a description of the weather \\(i.e. sunny, overcast, raining) is shown with a relevant image")
    public void aDescriptionOfTheWeatherIESunnyOvercastRainingIsShownWithARelevantImage() {
        ForecastResponse forecastResponse = (ForecastResponse) Objects.requireNonNull(resultActions.andReturn().getModelAndView()).getModel().get("forecastResponse");

        // It was cloudy in the JSON file :)
        assertEquals(description, forecastResponse.getAllWeatherResponses()[0].getDescription());

    }

    @And("the current temperature is shown")
    public void theCurrentTemperatureIsShown() {
        ForecastResponse forecastResponse = (ForecastResponse) Objects.requireNonNull(resultActions.andReturn().getModelAndView()).getModel().get("forecastResponse");

        assertEquals(temperature, forecastResponse.getAllWeatherResponses()[0].getTemperature());
    }

    @And("the current humidity is shown")
    public void theCurrentHumidityIsShown() {
        ForecastResponse forecastResponse = (ForecastResponse) Objects.requireNonNull(resultActions.andReturn().getModelAndView()).getModel().get("forecastResponse");

        assertEquals(humidity, forecastResponse.getAllWeatherResponses()[0].getHumidity());
    }
}
