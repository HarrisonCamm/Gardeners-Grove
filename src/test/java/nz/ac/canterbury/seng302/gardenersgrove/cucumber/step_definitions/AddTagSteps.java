package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;


import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ViewGardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class AddTagSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private Garden ownedGarden;

    private static Location location;

    private static User owner;

    private ResultActions resultActions;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        location = new Location("Test Location", "Test Address", "Test City", "Test Country", "Test Postcode");
        owner = new User("user@gmail.com", "Test", "User", "p@ssw0rd123");
    }

    @Given("I am a garden owner")
    public void iAmAGardenOwner() throws Exception {
        mockMvc.perform(post("/create-garden")
                .param("name", "Test Garden")
                .param("location.streetAddress", location.getStreetAddress())
                .param("location.suburb", location.getSuburb())
                .param("location.city", location.getCity())
                .param("location.postcode", location.getPostcode())
                .param("location.country", location.getCountry())
                .param("size", "Test Size"));
    }

    @And("I am on the garden details page for a garden I own to observe the tags feature")
    public void iAmOnTheGardenDetailsPageForAGardenIOwnToObserveTheTags() throws Exception {
        resultActions = mockMvc.perform(get("/view-garden")
                .param("gardenID", ownedGarden.getId().toString()));
    }

    @Then("there is a textbox where I can type in tags to the garden")
    public void thereIsATextboxWhereICanTypeInTagsToTheGarden() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(model().attribute("tagInput", ""));
    }
}