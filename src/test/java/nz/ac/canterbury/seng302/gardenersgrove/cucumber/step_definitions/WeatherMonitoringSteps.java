package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


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

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Given("I am a garden owner with in {string} in {string}")
    public void iAmAGardenOwnerWithInIn(String city, String country) throws Exception {

        resultActions = mockMvc.perform(post("/create-garden")
                .param("name", "Kaia's Garden")
                .param("location.city", city)
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
        
    }

    @Then("the current day of the week is shown")
    public void theCurrentDayOfTheWeekIsShown() {
        
    }

    @And("the current date is shown")
    public void theCurrentDateIsShown() {
        
    }

    @And("a description of the weather \\(i.e. sunny, overcast, raining) is shown with a relevant image")
    public void aDescriptionOfTheWeatherIESunnyOvercastRainingIsShownWithARelevantImage() {
        
    }

    @And("the current temperature is shown")
    public void theCurrentTemperatureIsShown() {
        
    }

    @And("the current humidity is shown")
    public void theCurrentHumidityIsShown() {
    }
}
