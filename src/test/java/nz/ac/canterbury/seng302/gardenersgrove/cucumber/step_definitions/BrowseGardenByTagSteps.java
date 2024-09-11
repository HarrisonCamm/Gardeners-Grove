package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")

public class BrowseGardenByTagSteps {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private GardenService gardenService;

    @Autowired
    private TagService tagService;

    private MockMvc mockMvc;
    private MvcResult mvcResult;
    private static Location location;


    private List<Tag> existingTags = new ArrayList<>();

    private ResultActions resultActions;
    private static Garden ownedGarden1;

    private static Garden ownedGarden2;

    private ModelAndView modelAndView;
    private static final String TAG_1 = "tagValid";
    private static final String TAG_2 = "tagAutocomplete";
    private static final String TAG_3 = "inaya garden";
    private static final String TAG_4 = "herbal";



    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        location = new Location("Test Location", "Test Address", "Test City", "1234", "Test Postcode");

    }
    //Background
    @And("there are public gardens with tags available")
    public void there_are_public_gardens_with_tags_available() throws Exception {
        ownedGarden1 = AddTagSteps.ownedGarden1;
        ownedGarden2 = AddTagSteps.ownedGarden2;
        add_tag_to_garden(TAG_1, ownedGarden1);
        add_tag_to_garden(TAG_2, ownedGarden1);
        add_tag_to_garden(TAG_3, ownedGarden2);
        add_tag_to_garden(TAG_4, ownedGarden2);

        // MockMvc doesn't do the redirect, so we need to get the garden again
        resultActions = mockMvc.perform(get("/view-garden")
                        .param("gardenID", ownedGarden1.getId().toString())
                        .with(csrf())) // Add CSRF token
                .andExpect(status().isOk());

        existingTags = tagService.getTags();
    }

    //Background
    @And("I am on the browse gardens page")
    public void i_am_on_the_browse_gardens_page() throws Exception {
        mvcResult = mockMvc.perform(get("/browse-gardens"))
                .andExpect(status().isOk())
                .andReturn();
    }

    //AC1
    @Then("I can select any number of tags to filter by")
    public void i_can_select_any_number_of_tags_to_filter_by() throws Exception {

        search_valid_tag(TAG_1);
        search_valid_tag(TAG_2);
        search_valid_tag(TAG_3);

        modelAndView = mvcResult.getModelAndView();
        @SuppressWarnings("unchecked")
        List<Tag> displayedTags = (List<Tag>) modelAndView.getModel().get("searchTags");
        Assertions.assertTrue(displayedTags.size() > 1);


    }
    //AC2
    @Given("I want to browse for a tag")
    public void i_want_to_browse_for_a_tag() {
        // not yet implemented
    }

    //AC2
    @When("I start typing the tag")
    public void i_start_typing_the_tag() {
        // not yet implemented
    }

    @Then("tags matching my input are shown")
    public void tags_matching_my_input_are_shown() {
        // not yet implemented
    }

    //AC2
    @Given("I am viewing autocomplete suggestions for my input {string}")
    public void i_am_viewing_autocomplete_suggestions_for_my_input(String input) {
        // not yet implemented
    }

    //AC3
    @When("I click on a suggestion {string}")
    public void i_click_on_a_suggestion(String suggestion) throws Exception {
        i_press_the_enter_key(suggestion);
    }

    //AC3 and AC4
    @Then("the tag {string} is added to my current selection")
    public void the_tag_is_added_to_my_current_selection(String input) {
        modelAndView = mvcResult.getModelAndView();
        List<?> uncheckedTags = (List<?>) Objects.requireNonNull(modelAndView).getModel().get("searchTags");

        Assertions.assertTrue(uncheckedTags.stream().allMatch(element -> element instanceof Tag));

        // Filter the list to contain only elements of type Tag to avoid unchecked cast error
        List<Tag> displayedTags = uncheckedTags.stream()
                .filter(element -> element instanceof Tag)
                .map(element -> (Tag) element)
                .collect(Collectors.toList());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(displayedTags),
                () -> Assertions.assertTrue(displayedTags.stream().anyMatch(eachTag -> eachTag.getName().equals(input)))
        );
    }

    //AC3 and AC4
    @And("the text field is cleared")
    public void the_text_field_is_cleared() {
        modelAndView = mvcResult.getModelAndView();
        String textFieldValue = (String) Objects.requireNonNull(modelAndView).getModel().get("tagsInput");
        Assertions.assertEquals("", textFieldValue);
    }

    //AC4
    @Given("I type out a tag {string} that already exists")
    public void i_type_out_a_tag_that_already_exists(String existingTagName) {
        Assertions.assertAll(
                () -> Assertions.assertNotNull(tagService.getTagByName(existingTagName)),
                () -> Assertions.assertTrue(existingTags.stream().anyMatch(eachTag -> eachTag.getName().equals(existingTagName)))
        );
    }

    //AC4, AC5, Removing a tag
    @When("I press the enter key with {string}")
    public void i_press_the_enter_key(String typedTag) throws Exception {
        resultActions = mockMvc.perform(get("/browse-gardens")
                .param("q", "")
                .param("tagsInput", typedTag))
                .andExpect(status().is2xxSuccessful());

        mvcResult = resultActions.andReturn();
    }

    //AC5
    @Given("I type out a tag {string} that does not exist")
    public void i_type_out_a_tag_that_does_not_exist(String nonExistentTagName) {
        Assertions.assertAll(
                () -> Assertions.assertNull(tagService.getTagByName(nonExistentTagName)),
                () -> Assertions.assertTrue(existingTags.stream().noneMatch(eachTag -> eachTag.getName().equals(nonExistentTagName)))
        );
    }

    //AC5 and Removing a tag
    @Then("no tag {string} is added to my current selection")
    public void no_tag_is_added_to_my_current_selection(String nonExistentTagName) {
        modelAndView = mvcResult.getModelAndView();
        List<?> uncheckedTags = (List<?>) Objects.requireNonNull(modelAndView).getModel().get("searchTags");

        Assertions.assertTrue(uncheckedTags.stream().allMatch(element -> element instanceof Tag));

        // Filter the list to contain only elements of type Tag to avoid unchecked cast error
        List<Tag> displayedTags = uncheckedTags.stream()
                .filter(element -> element instanceof Tag)
                .map(element -> (Tag) element)
                .toList();

        Assertions.assertTrue(displayedTags.stream().noneMatch(eachTag -> eachTag.getName().equals(nonExistentTagName)));
    }

    @And("the text field contains {string} and is not cleared")
    public void the_text_field_is_not_cleared(String nonExistentTagName) {
        modelAndView = mvcResult.getModelAndView();
        String textFieldValue = (String) Objects.requireNonNull(modelAndView).getModel().get("tagsInput");
        Assertions.assertEquals(nonExistentTagName, textFieldValue);
    }

    //AC5
    @And("an error message tells me No tag matching {string}")
    public void an_error_message_tells_me_no_tag_matching_input(String nonExistentTagName) {
        modelAndView = mvcResult.getModelAndView();
        String errorMessage = (String) Objects.requireNonNull(modelAndView).getModel().get("tagNotFoundError");

        Assertions.assertAll(
                () -> Assertions.assertNotNull(errorMessage),
                () -> Assertions.assertEquals("No tag matching " + nonExistentTagName, errorMessage)
        );
    }

    @Given("I submit the search form as detailed in U17")
    public void i_submit_the_search_form_as_detailed_in_u17() {
        // not yet implemented
    }

    @Then("only gardens that match the other search requirements and any of the tags I selected are shown in the results")
    public void only_gardens_that_match_the_other_search_requirements_and_any_of_the_tags_i_selected_are_shown_in_the_results() {
        // not yet implemented
    }

    //Removing a tag
    @When("I click the x button on the tag {string}")
    public void i_click_the_X_button_on_the_tag_input(String tagName) throws Exception {
        resultActions = mockMvc.perform(post("/browse-gardens")
                        .param("tagToRemove", tagName))
                .andExpect(status().is2xxSuccessful());

        mvcResult = resultActions.andReturn();
    }

    public void add_tag_to_garden(String tag, Garden garden) throws Exception {
        resultActions = mockMvc.perform(post("/add-tag")
                        .param("gardenID", garden.getId().toString())
                        .param("tag", tag) //match what is in the feature file examples
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                // Check that page redirects to a view garden of ANY number
                .andExpect(header().string("Location", Matchers.matchesPattern("/view-garden\\?gardenID=\\d+")));
    }

    private void search_valid_tag(String tagName) throws Exception {
        mvcResult = mockMvc.perform(get("/browse-gardens")
                        .param("tagsInput", tagName))
                .andExpect(status().isOk())
                .andReturn();
    }
}
