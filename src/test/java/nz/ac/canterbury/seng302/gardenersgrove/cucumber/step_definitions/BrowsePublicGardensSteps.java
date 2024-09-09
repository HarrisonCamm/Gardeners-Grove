package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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

        // Mark the garden as public
        publicGarden.setIsPublic(true);

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
        // Create the public garden link
        String gardenLink = "/view-garden?gardenID=" + publicGarden.getId();

        // Perform the GET request to view that garden
        mockMvc.perform(get(gardenLink))
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

    @When("I click the {string} button on the navigation bar")
    public void iClickTheButtonOnTheNavigationBar(String browserGardens) throws Exception {
        // change button name to actual url
        browserGardens = "/browse-gardens";

        // Click the browse gardens button
        mockMvc.perform(get(browserGardens).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("gardenPage"));
    }

    // AC2
    @Then("I am taken to a page with a search text box and the {int} or fewer of newest created gardens")
    public void iAmTakenToAPageWithASearchTextBoxAndTheOrFewerOfNewestCreatedGardens(Integer maxGardens) throws Exception {
        // Perform the GET request to the "/browse-gardens" endpoint
        mockMvc.perform(get("/browse-gardens"))
                .andExpect(status().isOk()) // Check that the status is OK (200)
                .andExpect(MockMvcResultMatchers.model().attributeExists("gardenPage")) // Ensure the gardenPage attribute exists

                // Adjust the matcher to accept any number of gardens between 1 and maxGardens

                // Ensure at least 1 garden is present
                .andExpect(MockMvcResultMatchers.model().attribute("gardenPage", hasProperty("content", hasSize(greaterThanOrEqualTo(1)))))

                // Ensure no more than maxGardens are displayed
                .andExpect(MockMvcResultMatchers.model().attribute("gardenPage", hasProperty("content", hasSize(lessThanOrEqualTo(Math.min(maxGardens, 10))))));
    }

    // AC3
    @Given("I enter a search string {string} into the search box")
    public void iEnterASearchStringIntoTheSearchBox(String arg0) {

    }

    // AC3
    @When("I click the search button labelled {string} or the magnifying glass icon")
    public void iClickTheSearchButtonLabelledOrTheMagnifyingGlassIcon(String arg0) {

    }

    // AC3
    @Then("I am shown only gardens whose names or plants include {string}")
    public void iAmShownOnlyGardensWhoseNamesOrPlantsInclude(String arg0) {
    }
}

