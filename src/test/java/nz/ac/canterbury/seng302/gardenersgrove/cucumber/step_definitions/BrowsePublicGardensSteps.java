package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class BrowsePublicGardensSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private GardenService gardenService;

    @Autowired
    private UserService userService;

    private MockMvc mockMvc;

    private Garden publicGarden;

    private User gardenOwner;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // get a logged-in user
        gardenOwner = userService.getAuthenticatedUser();
    }

    // AC1
    @Given("a garden has been marked as public")
    public void gardenMarkedAsPublic() {
        // Create test location
        Location location = new Location("Test Street", "Test Suburb", "Test City", "12345", "Country");

        // Create a test garden
        publicGarden = new Garden("Public Test Garden", location, "100", gardenOwner, "A public garden");

        // Set garden public
        publicGarden.setIsPublic(true);

        // Save garden to a database
        gardenService.addGarden(publicGarden);
    }

    // AC1
    @Then("any logged-in user can view the name, size, and plants when clicking on a link to the garden")
    public void loggedInUserCanViewGardenDetails() throws Exception {
        String gardenLink = "/view-garden?gardenID=" + publicGarden.getId();
        mockMvc.perform(get(gardenLink).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("gardenName", "gardenSize", "plants"))
                .andExpect(MockMvcResultMatchers.model().attribute("gardenName", publicGarden.getName()))
                .andExpect(MockMvcResultMatchers.model().attribute("gardenSize", publicGarden.getSize()));
    }
}

