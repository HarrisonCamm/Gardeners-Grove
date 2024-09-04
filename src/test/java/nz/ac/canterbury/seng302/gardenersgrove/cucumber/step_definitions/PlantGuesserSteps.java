package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.PlantGuesserController;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantGuesserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @Autowired
    PlantGuesserController plantGuesserController;
    private MockMvc mockMvc;
    private ResultActions resultActions;
    private MvcResult mvcResult;
    private ModelAndView modelAndView;
    private String validPlantJsonString;
    private String validPlantFamilyJsonString;
    private String plantName;
    private String plantImage;
    private List<String> familyMembersCommonNames;
    private int roundNumber = 1;
    @Before
    public void setup() throws IOException {
        validPlantJsonString = Files.readString(Paths.get("src/test/resources/json/getPlantsResponse.json"));
        validPlantFamilyJsonString = Files.readString(Paths.get("src/test/resources/json/getPlantFamilyResponse.json"));
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // This makes the shuffling of plant guesser options not random so it can be tested
        Random fixedRandom = new Random(13);
        plantGuesserController.setRandom(fixedRandom);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode1 = objectMapper.readTree(validPlantJsonString);
        plantName = jsonNode1.get("data").get(0).get("common_name").asText();
        plantImage = jsonNode1.get("data").get(0).get("image_url").asText();

        familyMembersCommonNames = new ArrayList<>();
        JsonNode jsonNode2 = objectMapper.readTree(validPlantFamilyJsonString);
        for (int i=0; i < jsonNode2.get("data").size(); i++) {
            String plantFamilyMemberName = jsonNode2.get("data").get(i).get("common_name").asText();
            familyMembersCommonNames.add(plantFamilyMemberName);
        }

    }

    @Given("I am on the Games page")
    public void i_am_on_the_games_page() throws Exception {
        mvcResult = mockMvc.perform(get("/games"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @When("I go to the Plant Guesser game page")
    public void i_go_to_the_plant_guesser_game_page() throws Exception {
        resultActions = mockMvc.perform(get("/plant-guesser"));
        mvcResult = resultActions.andExpect(status().isOk())
                .andReturn();
        modelAndView = mvcResult.getModelAndView();
    }

    @Then("I see an image of a plant")
    public void i_see_an_image_of_a_plant() {
        String displayedImage = (String) Objects.requireNonNull(modelAndView).getModel().get("plantImage");
        Assertions.assertEquals(plantImage, displayedImage);
    }

    @And("I see four options of plant names where one is the correct plant name and the other three are names of plants in the same family to click on")
    public void i_see_four_options_of_plant_names() {
        List<String[]> options = (List<String[]>) modelAndView.getModel().get("quizOptions");

        // checks that the quiz options contain the correct plant name, and gets the index of it to ignore it when checking the other plants
        int correctPlantIndex = IntStream.range(0, options.size())
                .filter(i -> options.get(i)[0].contains(plantName))
                .findFirst()
                .orElse(-1);  // returns -1 if options doesn't contain the correct plant name

        Assertions.assertNotEquals(-1, correctPlantIndex);

        // checks the other quiz options to make sure they're in the plant family
        IntStream.range(0, options.size())
                .filter(i -> i != correctPlantIndex) // skips the index of correct plant
                .forEach(i -> {
                    String option = options.get(i)[0];
                    boolean containsCommonName = familyMembersCommonNames.stream()
                            .anyMatch(option::contains);

                    Assertions.assertTrue(containsCommonName);
                });


    }

    @And("I see a text description saying Plant X {string}")
    public void i_see_a_text_description_saying_x_of_of_10(String totalRounds) throws Exception {
        resultActions.andExpect(content().string(org.hamcrest.Matchers
                                .containsString("Plant " + roundNumber + totalRounds)));
    }

    @And("I see a text description saying {string}")
    public void i_see_a_text_description_saying(String description) throws Exception {
        resultActions.andExpect(content().string(org.hamcrest.Matchers
                .containsString(description)));
    }

    @Given("I am on the Plant Guesser game page")
    public void i_am_on_the_plant_guesser_game_page() {
        //not yet implemented
    }

    @When("I select the correct plant name")
    public void i_select_the_correct_plant_name() {
        //not yet implemented
    }

    @Then("I am shown a message saying {string}")
    public void i_am_shown_a_message_saying(String arg0) {
        //not yet implemented
    }

    @And("the option I selected is shown as green")
    public void the_option_i_selected_is_shown_as_green() {
        //not yet implemented
    }

    @And("a Next Question button \\(could be an icon) is shown")
    public void a_next_question_button_could_be_an_icon_is_shown() {
        //not yet implemented
    }

    @When("I click the Next Question button after guessing")
    public void i_click_the_next_question_button_after_guessing() {
        //not yet implemented
    }

    @Then("I am shown a new image of a plant and four options of plant names where one is the correct plant name and the other three are names of plants in the same family to click on")
    public void i_am_shown_a_new_image_of_a_plant_and_four_options_of_plant_names_where_one_is_the_correct_plant_name_and_the_other_three_are_names_of_plants_in_the_same_family_to_click_on() {
        //not yet implemented
    }

    @And("I see a text description saying Plant X+{int} {string}")
    public void i_see_a_text_description_saying_plant_X_1(int nextRound, String totalRounds) {
        //not yet implemented
    }

    @When("I select an incorrect plant name option")
    public void i_select_an_incorrect_plant_name_option() {
        //not yet implemented
    }

    @And("the option I selected is shown as red")
    public void the_option_i_selected_is_shown_as_red() {
        //not yet implemented
    }

    @When("I have guessed for 10 plants \\(completed the game)")
    public void i_have_guessed_for_10_plants_completed_the_game() {
        //not yet implemented
    }

    @Then("an additional message is shown below any other messages {string}")
    public void an_additional_message_is_shown_below_any_other_messages(String arg0) {
        //not yet implemented
    }

    @And("I see my score of correct guesses out of 10")
    public void i_see_my_score_of_correct_guesses_out_of_10() {
        //not yet implemented
    }

    @And("my total Bloom count is updated and displayed")
    public void my_total_bloom_count_is_updated_and_displayed() {
        //not yet implemented
    }

    @When("I click the Back button \\(could be an icon)")
    public void i_click_the_back_button_could_be_an_icon() {
        //not yet implemented
    }

    @Then("I am taken back to the Games page")
    public void i_am_taken_back_to_the_games_page() {
        //not yet implemented
    }

    @And("my total Blooms are displayed")
    public void my_total_blooms_are_displayed() {
        //not yet implemented
    }

    @And("my current game progress is not saved")
    public void my_current_game_progress_is_not_saved() {
        //not yet implemented
    }
}
