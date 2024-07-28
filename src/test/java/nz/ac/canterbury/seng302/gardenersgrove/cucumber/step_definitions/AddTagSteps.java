package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ViewGardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AddTagSteps {

    @Autowired
    private static ViewGardenController viewGardenController;

    private static MockMvc mockMvcViewGarden;

    private static Garden ownedGarden;

    private static Location location;

    private static User owner;

    @BeforeAll
    public static void setup() {

        mockMvcViewGarden = MockMvcBuilders.standaloneSetup(viewGardenController).build();

        location = new Location("Test Location", "Test Address", "Test City", "Test Country", "Test Postcode");
        owner = new User("user@gmail.com", "Test", "User", "p@ssw0rd123");
    }

    @Given("I am a garden owner")
    public void iAmAGardenOwner() {
        ownedGarden = new Garden("Test Garden", location, "100", owner);
    }

    @And("I am on the garden details page for a garden I own to observe the tags feature")
    public void iAmOnTheGardenDetailsPageForAGardenIOwnToObserveTheTags() {
    }

    @Then("there is a textbox where I can type in tags to the garden")
    public void thereIsATextboxWhereICanTypeInTagsToTheGarden() {

    }
}
