package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantGuesserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class PlantGuesserSteps {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    PlantGuesserService plantGuesserService;
    private MockMvc mockMvc;

    private MvcResult mvcResult;
    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }

    @Given("I am on the Games page")
    public void i_am_on_the_games_page() {
        // can't test til merged with u6004
    }

    @When("I go to the Plant Guesser game page")
    public void i_go_to_the_plant_guesser_game_page() throws Exception {
        mvcResult = mockMvc.perform(get("/plant-guesser"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Then("I see an image of a plant and four options of plant names where one is the correct plant name and the other three are names of plants in the same family to click on")
    public void i_see_an_image_of_a_plant_and_four_options_of_plant_names() {

    }

    @And("I see a text description saying {string}")
    public void i_see_a_text_description_saying(String arg0) {
    }

    @Given("I am on the Plant Guesser game page")
    public void i_am_on_the_plant_guesser_game_page() {
    }

    @When("I select the correct plant name")
    public void i_select_the_correct_plant_name() {
    }

    @Then("I am shown a message saying {string}")
    public void i_am_shown_a_message_saying(String arg0) {
    }

    @And("the option I selected is shown as green")
    public void the_option_i_selected_is_shown_as_green() {
    }

    @And("a Next Question button \\(could be an icon) is shown")
    public void a_next_question_button_could_be_an_icon_is_shown() {
    }

    @When("I click the Next Question button after guessing")
    public void i_click_the_next_question_button_after_guessing() {
    }

    @Then("I am shown a new image of a plant and four options of plant names where one is the correct plant name and the other three are names of plants in the same family to click on")
    public void i_am_shown_a_new_image_of_a_plant_and_four_options_of_plant_names_where_one_is_the_correct_plant_name_and_the_other_three_are_names_of_plants_in_the_same_family_to_click_on() {
    }

    @When("I select an incorrect plant name option")
    public void i_select_an_incorrect_plant_name_option() {
    }

    @And("the option I selected is shown as red")
    public void the_option_i_selected_is_shown_as_red() {
    }

    @When("I have guessed for 10 plants \\(completed the game)")
    public void i_have_guessed_for_10_plants_completed_the_game() {
    }

    @Then("an additional message is shown below any other messages {string}")
    public void an_additional_message_is_shown_below_any_other_messages(String arg0) {
    }

    @And("I see my score of correct guesses out of 10")
    public void i_see_my_score_of_correct_guesses_out_of_10() {
    }

    @And("my total Bloom count is updated and displayed")
    public void my_total_bloom_count_is_updated_and_displayed() {
    }

    @When("I click the Back button \\(could be an icon)")
    public void i_click_the_back_button_could_be_an_icon() {
    }

    @Then("I am taken back to the Games page")
    public void i_am_taken_back_to_the_games_page() {
    }

    @And("my total Blooms are displayed")
    public void my_total_blooms_are_displayed() {
    }

    @And("my current game progress is not saved")
    public void my_current_game_progress_is_not_saved() {
    }

}
