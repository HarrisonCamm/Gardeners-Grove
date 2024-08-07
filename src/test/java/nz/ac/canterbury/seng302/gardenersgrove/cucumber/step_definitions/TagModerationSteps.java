package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.suite.api.ExcludePackages;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;


import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SuppressWarnings({"unchecked", "SpringJavaInjectionPointsAutowiringInspection"})
public class TagModerationSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private GardenService gardenService;

    @Autowired
    private ModerationService moderationService;

    @Autowired
    private TagService tagService;

    private MockMvc mockMvc;

    private MvcResult mvcResult;

    private static Location location;

    private static Garden ownedGarden;

    private static String typedTag;

    private ResultActions resultActions;
    private int inappropriateTagCount;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }

    @Given("RAR")
    public void RAR() {
        ArrayList<Garden> gardens = (ArrayList<Garden>) gardenService.getOwnedGardens(userService.getAuthenticatedUser().getUserId());
        ownedGarden = gardens.get(gardens.size() - 1);
        tagService.evaluateWaitingTags();
    }

    @Given("I am adding a valid tag")
    public void i_am_adding_a_valid_tag() {
        typedTag = "tim tams fr";
    }

    @Given("I am adding a inappropriate tag")
    public void i_am_adding_a_inappropriate_tag() {
        // Write code here that turns the phrase above into concrete actions
        typedTag = "InappropriateTag";
    }


    @When("I confirm the tag")
    public void i_confirm_the_tag() throws Exception {
        // Assuming confirm means clicking the button
        mvcResult = mockMvc.perform(post("/add-tag")
                .param("tag", typedTag)
                .param("gardenID", ownedGarden.getId().toString())
                .with(csrf()))
                .andReturn(); // Add CSRF token
    }

    @Then("the tag is checked for offensive or inappropriate words")
    public void the_tag_is_checked_for_offensive_or_inappropriate_words() {
        verify(moderationService).moderateText(Mockito.any(String.class));
    }


    @Then("an error message tells me that the submitted word is not appropriate")
    public void an_error_message_tells_me_that_the_submitted_word_is_not_appropriate() {
        // Write code here that turns the phrase above into concrete actions
        Assertions.assertEquals("Profanity or inappropriate language detected", mvcResult.getModelAndView().getModel().get("profanityTagError"));
    }

    @Then("the tag is not added to the list of user-defined tags")
    public void the_tag_is_not_added_to_the_list_of_user_defined_tags() {
        List<Tag> tags = tagService.getTags();
        boolean hasInappropriateTag = tags.stream()
                .anyMatch(tag -> "InappropriateTag".equals(tag.getName()));
        Assertions.assertFalse(hasInappropriateTag);
    }

    @Given("the submitted tag cannot be evaluated for appropriateness")
    public void the_submitted_tag_cannot_be_evaluated_for_appropriateness() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        typedTag = "NotEvaluated";
        resultActions = mockMvc.perform(post("/add-tag")
                        .param("tag", typedTag)
                        .param("gardenID", ownedGarden.getId().toString())
                        .with(csrf())) ;
        resultActions = mockMvc.perform(get("/view-garden")
                .param("gardenID", ownedGarden.getId().toString())
                        .session((MockHttpSession) resultActions.andReturn().getRequest().getSession())
                .with(csrf())); // Add CSRF token)
        String evaluationError = (String) resultActions.andReturn().getModelAndView().getModel().get("tagError");
        Assertions.assertEquals("Tag could not be evaluated at this time and will be reviewed shortly.", evaluationError);
    }

    @Then("the tag is not visible publicly")
    public void the_tag_is_not_visible_publicly() throws Exception{
        // Write code here that turns the phrase above into concrete actions
        resultActions = mockMvc.perform(get("/view-garden")
                        .param("gardenID", ownedGarden.getId().toString())
                        .with(csrf())); // Add CSRF token)
        List<Tag> tags = (List<Tag>) resultActions.andReturn().getModelAndView().getModel().get("gardenTags");

        boolean hasInappropriateTag = tags.stream()
                .anyMatch(tag -> typedTag.equals(tag.getName()));
        Assertions.assertFalse(hasInappropriateTag);
    }

    @Then("it is added to a waiting list that will be evaluated as soon as possible")
    public void it_is_added_to_a_waiting_list_that_will_be_evaluated_as_soon_as_possible() {
        // Write code here that turns the phrase above into concrete actions
        List<Tag> waitingTags = tagService.getTagsByEvaluated(false);
        boolean hasInappropriateTag = waitingTags.stream()
                .anyMatch(tag -> typedTag.equals(tag.getName()));
        Assertions.assertTrue(hasInappropriateTag);
    }

    @Given("the evaluation of a user-defined tag {string} was delayed")
    public void the_evaluation_of_a_user_defined_tag_was_delayed(String inputTag) throws Exception {
        typedTag = inputTag;
        inappropriateTagCount = userService.getAuthenticatedUser().getInappropriateTagCount();
        resultActions = mockMvc.perform(post("/add-tag")
                .param("tag", typedTag)
                .param("gardenID", ownedGarden.getId().toString())
                .with(csrf())) ;
        Tag delayedTag = tagService.getTagByName(typedTag);
        delayedTag.setEvaluated(false);
        tagService.addTag(delayedTag);
        List<Tag> waitingTags = tagService.getTagsByEvaluated(false);
        boolean hasDelayedTag = waitingTags.stream()
                .anyMatch(tag -> typedTag.equals(tag.getName()));
        Assertions.assertTrue(hasDelayedTag);
    }

    @When("the tag has been evaluated as appropriate")
    public void the_tag_has_been_evaluated_as_appropriate() {
        tagService.evaluateWaitingTags();
        List<Tag> tags = tagService.getTagsByEvaluated(true);
        boolean hasTag = tags.stream()
                .anyMatch(tag -> typedTag.equals(tag.getName()));
        Assertions.assertTrue(hasTag);
    }

    @Then("the tag is visible publicly on the garden it was assigned to")
    public void the_tag_is_visible_publicly_on_the_garden_it_was_assigned_to() throws Exception {
        resultActions = mockMvc.perform(get("/view-garden")
                .param("gardenID", ownedGarden.getId().toString())
                .with(csrf()));
        List<Tag> tags = (List<Tag>) resultActions.andReturn().getModelAndView().getModel().get("gardenTags");
        boolean hasTag = tags.stream()
                .anyMatch(tag -> typedTag.equals(tag.getName()));
        Assertions.assertTrue(hasTag);
    }

    @Then("it is added to the list of user-defined tags")
    public void it_is_added_to_the_list_of_user_defined_tags() {
        List<Tag> tags = tagService.getTagsByEvaluated(true);
        boolean hasTag = tags.stream()
                .anyMatch(tag -> typedTag.equals(tag.getName()));
        Assertions.assertTrue(hasTag);
    }

    @When("the tag has been evaluated as inappropriate")
    public void the_tag_has_been_evaluated_as_inappropriate() {
        tagService.evaluateWaitingTags();
        List<Tag> tags = tagService.getTags();
        boolean hasTag = tags.stream()
                .anyMatch(tag -> typedTag.equals(tag.getName()));
        Assertions.assertFalse(hasTag);
    }

    @Then("the tag is removed from the garden it was assigned to")
    public void the_tag_is_removed_from_the_garden_it_was_assigned_to() {
        List<Tag> gardenTags = gardenService.getTags(ownedGarden.getId());
        boolean hasTag = gardenTags.stream()
                .anyMatch(tag -> typedTag.equals(tag.getName()));
        Assertions.assertFalse(hasTag);
    }

    @Then("it is not added to the list of user-defined tags")
    public void it_is_not_added_to_the_list_of_user_defined_tags() {
        List<Tag> tags = tagService.getTags();
        boolean hasTag = tags.stream()
                .anyMatch(tag -> typedTag.equals(tag.getName()));
        Assertions.assertFalse(hasTag);
    }

    @Then("the user’s count of inappropriate tags is increased by 1")
    public void the_user_s_count_of_inappropriate_tags_is_increased_by() {
        int updatedCount = userService.getAuthenticatedUser().getInappropriateTagCount();
        Assertions.assertEquals(inappropriateTagCount+ 1, updatedCount);
    }
}
