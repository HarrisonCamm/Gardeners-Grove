package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class AddTagSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private GardenService gardenService;

    @Autowired
    private TagService tagService;

    private MockMvc mockMvc;

    private static Location location;

    private static Garden ownedGarden1;

    private static Garden ownedGarden2;

    private static String typedTag;

    private static List<Tag> tagList;

    private ResultActions resultActions;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        location = new Location("Test Location", "Test Address", "Test City", "1234", "Test Postcode");
    }

    @Given("I am a garden owner")
    public void iAmAGardenOwner() throws Exception {

        resultActions = mockMvc.perform(post("/create-garden")
                        .param("name", "Test Garden 1")
                        .param("location.streetAddress", location.getStreetAddress())
                        .param("location.suburb", location.getSuburb())
                        .param("location.city", location.getCity())
                        .param("location.postcode", location.getPostcode())
                        .param("location.country", location.getCountry())
                        .param("size", "10")
                        .with(csrf())); // Add CSRF token

        mockMvc.perform(post("/create-garden")
                .param("name", "Test Garden 2")
                .param("location.streetAddress", location.getStreetAddress())
                .param("location.suburb", location.getSuburb())
                .param("location.city", location.getCity())
                .param("location.postcode", location.getPostcode())
                .param("location.country", location.getCountry())
                .param("size", "10")
                .with(csrf())); // Add CSRF token

        ArrayList<Garden> gardens = (ArrayList<Garden>) gardenService.getOwnedGardens(userService.getAuthenticatedUser().getUserId());
        ownedGarden1 = gardens.get(gardens.size() - 2);
        ownedGarden2 = gardens.get(gardens.size() - 1);
        tagList = tagService.getTags();
        assertNotNull(gardens);
    }

    @And("I am on the garden details page for a garden I own to observe the tags feature")
    public void iAmOnTheGardenDetailsPageForAGardenIOwnToObserveTheTags() throws Exception {
        resultActions = mockMvc.perform(get("/view-garden")
                        .param("gardenID", ownedGarden1.getId().toString())
                        .with(csrf())) // Add CSRF token
                .andExpect(status().isOk());
    }

    @Then("there is a text box where I can type in tags to the garden")
    public void thereIsATextBoxWhereICanTypeInTagsToTheGarden() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(model().attribute("tagInput", ""));
    }

    @Given("I am on the garden details page for a public garden")
    public void iAmOnTheGardenDetailsPageForAPublicGarden() {
        // TODO: Implement this step once the garden details page is implemented
    }

    @Then("I can see a list of tags that the garden has been marked with by its owner")
    public void iCanSeeAListOfTagsThatTheGardenHasBeenMarkedWithByItsOwner() {
        // TODO: Implement this step once the garden details page is implemented
    }

    @Given("I have already created a tag for a garden I own")
    public void iHaveAlreadyCreatedATagForAGardenIOwn() throws Exception {
        resultActions = mockMvc.perform(post("/add-tag")
                .param("gardenID", ownedGarden1.getId().toString())
                .param("tag", "Test Tag")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                // Check that page redirects to a view garden of ANY number
                .andExpect(header().string("Location", Matchers.matchesPattern("/view-garden\\?gardenID=\\d+")));

        // MockMvc doesn't do the redirect, so we need to get the garden again
        resultActions = mockMvc.perform(get("/view-garden")
                        .param("gardenID", ownedGarden1.getId().toString())
                        .with(csrf())) // Add CSRF token
                .andExpect(status().isOk());
    }

    @When("I type a tag into the text box that matches the tag I created")
    public void iTypeATagIntoTheTextBoxThatMatchesTheTagICreated() {
        typedTag = "Test";
    }
    @Given("I have typed a tag into the text box that matches the tag I created")
    public void iHaveTypedATagIntoTheTextBoxThatMatchesTheTagICreated() {
        typedTag = "Test";
    }

    @Then("I should see autocomplete options for tags that already exist in the system")
    public void iShouldSeeAutocompleteOptionsForTagsThatAlreadyExistInTheSystem() throws Exception {
        // Get the latest result from the /view-garden endpoint
        resultActions = mockMvc.perform(get("/view-garden")
                        .param("gardenID", ownedGarden2.getId().toString())
                        .with(csrf()))
                .andExpect(status().isOk());

        // Get all tags from the model
        List<Tag> allTags = (List<Tag>) resultActions.andReturn().getModelAndView().getModel().get("allTags");
        assertNotNull(allTags, "allTags should not be null");

        // Iterate through all tags and check if the typed tag is at the start
        for (Tag tag : allTags) {
            assertTrue(tag.getName().startsWith(typedTag));
        }
    }

    @When("I click on one suggestion")
    public void iClickOnOneSuggestion() throws Exception {
        resultActions = mockMvc.perform(post("/add-tag")
                        .param("gardenID", ownedGarden2.getId().toString())
                        .param("tag", "Test Tag")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
        typedTag = "Test Tag";
    }
    @Then("the tag is added to my garden")
    public void thatTagIsAddedToMyGarden() throws Exception {
        // MockMvc doesn't do the redirect, so we need to get the garden again
        resultActions = mockMvc.perform(get("/view-garden")
                        .param("gardenID", ownedGarden2.getId().toString())
                        .with(csrf())) // Add CSRF token
                .andExpect(status().isOk());
        // Get all tags from the model
        List<Tag> gardenTagsShown = (List <Tag>) resultActions.andReturn().getModelAndView().getModel().get("gardenTags");

        // Assert garden contains tag
        Assertions.assertTrue(gardenTagsShown.stream().anyMatch(gardenTag -> typedTag.equals(gardenTag.getName())));

        List<Tag> gardenTags = gardenService.getTags(ownedGarden2.getId());
        Assertions.assertTrue(gardenTags.stream().anyMatch(gardenTag -> typedTag.equals(gardenTag.getName())));
    }

    @And("the text box is cleared")
    public void theTextBoxIsCleared() {
        // Get the tagInput field from the model
        String tagInput = (String) resultActions.andReturn().getModelAndView().getModel().get("tagInput");

        // Assert that the tagInput field is empty
        assertTrue(tagInput.isEmpty());
    }

    @Given("I have entered valid text for a tag {string} that does not exist")
    public void iHaveEnteredValidTextForATagTagThatDoesNotExist(String validTag) {
        List<Tag> existingTags = tagService.getTags();
        Assertions.assertFalse(existingTags.stream().anyMatch(existingTag -> validTag.equals(existingTag.getName())));
        typedTag = validTag;

    }

    @And("the tag becomes a new user-defined tag on the system showing up in future auto-complete suggestions")
    public void theTagBecomesANewUserDefinedTagOnTheSystemShowingUpInFutureAutoCompleteSuggestions() {
        List<Tag> existingTags = tagService.getTags();
        Assertions.assertTrue(existingTags.stream().anyMatch(existingTag -> typedTag.equals(existingTag.getName())));

    }

    @Given("I have entered invalid text {string}")
    public void iHaveEnteredInvalidText(String invalidTag) {
        typedTag = invalidTag;
    }

    @When("I click the + button or press enter")
    public void iClickThePlusButtonOrPressEnter() throws Exception{
        resultActions = mockMvc.perform(post("/add-tag")
                        .param("gardenID", ownedGarden2.getId().toString())
                        .param("tag", typedTag)
                        .with(csrf()));
    }

    @Then("a tag error message {string} tells me {string}")
    public void aTagErrorMessageTellsMe(String errorType, String message) throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(model().attribute(errorType, message));

    }

    @Given("I have entered a tag that is more than 25 characters long")
    public void iHaveEnteredLongTag(){
        typedTag = "This is a very long tag that is over 25 characters long";
    }


    @And("no tag is added to my garden")
    public void noTagIsAddedToMyGarden() {

        // Verify that no tag is added to the garden
        List<Tag> gardenTags = (List<Tag>) resultActions.andReturn().getModelAndView().getModel().get("gardenTags");
        Assertions.assertFalse(gardenTags.stream().anyMatch(gardenTag -> typedTag.equals(gardenTag.getName())));
    }

    @And("no tag is added to the user defined tags the system knows")
    public void noTagIsAddedToTheUserDefinedTagsTheSystemKnows() {
        // Verify that no tag is added to the user-defined tags
        List<Tag> userTags = (List<Tag>) resultActions.andReturn().getModelAndView().getModel().get("allTags");
        Assertions.assertFalse(userTags.stream().anyMatch(userTag -> typedTag.equals(userTag.getName())));

    }

}
