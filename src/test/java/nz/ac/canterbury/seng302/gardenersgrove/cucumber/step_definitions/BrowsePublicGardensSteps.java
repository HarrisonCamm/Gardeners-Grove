package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class BrowsePublicGardensSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private GardenService gardenService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlantService plantService;

    private MockMvc mockMvc;

    private Garden publicGarden;

    private User gardenOwner;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create a test garden owner
        User gardenOwner = new User(
                "inaya@email.com",  // Email
                "Inaya",                    // First name
                true,                       // No last name
                "",                         // Empty last name
                "Password1!",               // Password
                "1990-01-01"                // Date of birth
        );

        // Save user to a database
        userService.addUser(gardenOwner);

        // Create test location
        Location location = new Location("Test Street", "Test Suburb", "Test City", "1234", "Country");

        // Create a test garden
        publicGarden = new Garden("Public Test Garden", location, "100", gardenOwner, "A public garden");

        // Save garden to a database
        gardenService.addGarden(publicGarden);

        // Create and add plants to the garden
        Plant testPlant = new Plant(publicGarden, "TestPlant", "5", "Testy plant", "2024-09-05");

        // Save plants to the database
        plantService.addPlant(testPlant);

        // Save garden to a database
        gardenService.addGarden(publicGarden);
    }

    // AC1
    @Given("a garden has been marked as public")
    public void gardenMarkedAsPublic() {
        // Set garden public
        publicGarden.setIsPublic(true);

        // Save garden change to a database
        gardenService.addGarden(publicGarden);
    }

    // AC1
    @Then("any logged-in user can view the name, size, and plants when clicking on a link to the garden")
    public void loggedInUserCanViewGardenDetails() throws Exception {
        String gardenLink = "/view-garden?gardenID=" + publicGarden.getId();
        mockMvc.perform(get(gardenLink).with(csrf())) // CSRF token is what authenticates the user
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenName", "gardenSize", "plants"))
                .andExpect(model().attribute("gardenName", publicGarden.getName()))
                .andExpect(model().attribute("gardenSize", publicGarden.getSize()))
                // Ensure there's exactly one plant
                .andExpect(model().attribute("plants", hasSize(1)))
                // Check the properties of the plant
                .andExpect(model().attribute("plants", hasItem(
                        allOf(
                                hasProperty("name", is("TestPlant")),
                                hasProperty("count", is("5")),
                                hasProperty("description", is("Testy plant")),
                                hasProperty("datePlanted", is("2024-09-05"))
                        )
                )));
    }

    // AC2
    @Given("I am anywhere on the system")
    public void iAmAnywhereOnTheSystem() {
        // Assuming user is logged in and navigating the system
    }

    // AC2
    @Then("I click the {string} button")
    public void clickBrowseGardensButton(String buttonLabel) throws Exception {
        // Trigger browse gardens page loading
        mockMvc.perform(get("/browse-gardens"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("gardenPage"));
    }
}

