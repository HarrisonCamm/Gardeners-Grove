package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.PlantGuesserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
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
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private String plantNameJson;
    private String plantImageJson;
    private List<String> familyMembersCommonNames;
    private List<String[]> quizOptions;
    private String plantFamily;
    private String quizOption1;
    private String quizOption2;
    private String quizOption3;
    private String quizOption4;
    private String plantImage;
    private String imageCredit;
    private int roundNumber;
    private int correctOption;
    private int score;
    private String plantImagePrevious;
    private List<String[]> quizOptionsPrevious;
    private int correctOptionPrevious;
    private int roundNumberPrevious;
    private User currentUser;
    @Before
    public void setup() throws IOException {
        String validPlantJsonString = Files.readString(Paths.get("src/test/resources/json/getPlantsResponse.json"));
        String validPlantFamilyJsonString = Files.readString(Paths.get("src/test/resources/json/getPlantFamilyResponse.json"));
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // This makes the shuffling of plant guesser options not random so it can be tested
        Random fixedRandom = new Random(13);
        plantGuesserController.setRandom(fixedRandom);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode1 = objectMapper.readTree(validPlantJsonString);
        plantNameJson = jsonNode1.get("data").get(0).get("common_name").asText();
        plantImageJson = jsonNode1.get("data").get(0).get("image_url").asText();

        familyMembersCommonNames = new ArrayList<>();
        JsonNode jsonNode2 = objectMapper.readTree(validPlantFamilyJsonString);
        for (int i=0; i < jsonNode2.get("plant1_family").get("data").size(); i++) {
            String plantFamilyMemberName = jsonNode2.get("plant1_family").get("data").get(i).get("common_name").asText();
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
        get_model_data();
    }

    @Then("I see an image of a plant")
    public void i_see_an_image_of_a_plant() {
        Assertions.assertEquals(plantImageJson, plantImage);
    }

    @And("I see four options of plant names where one is the correct plant name and the other three are names of plants in the same family to click on")
    public void i_see_four_options_of_plant_names() {

        // checks that the quiz options contain the correct plant name, and gets the index of it to ignore it when checking the other plants
        int correctPlantIndex = IntStream.range(0, quizOptions.size())
                .filter(i -> quizOptions.get(i)[0].contains(plantNameJson))
                .findFirst()
                .orElse(-1);  // returns -1 if options doesn't contain the correct plant name

        Assertions.assertNotEquals(-1, correctPlantIndex);

        // checks the other quiz options to make sure they're in the plant family
        IntStream.range(0, quizOptions.size())
                .filter(i -> i != correctPlantIndex) // skips the index of correct plant
                .forEach(i -> {
                    String option = quizOptions.get(i)[0];
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

    @When("I select the correct plant name")
    public void i_select_the_correct_plant_name() throws Exception {
        resultActions = mockMvc.perform(post("/plant-guesser")
                .param("selectedOption", String.valueOf(correctOption))
                .param("plantFamily", plantFamily)
                .param("quizOption1", quizOption1)
                .param("quizOption2", quizOption2)
                .param("quizOption3", quizOption3)
                .param("quizOption4", quizOption4)
                .param("plantImage", plantImage)
                .param("imageCredit", imageCredit)
                .param("roundNumber", String.valueOf(roundNumber))
                .param("correctOption", String.valueOf(correctOption))
                .param("score", String.valueOf(score)));

        mvcResult = resultActions.andExpect(status().isOk())
                .andReturn();
        get_model_data();

    }

    @Then("I am shown a message saying {string}")
    public void i_am_shown_a_message_saying(String answerMessage) throws Exception {
        resultActions.andExpect(content().string(org.hamcrest.Matchers
                .containsString(answerMessage)));
    }

    @And("the option I selected is shown as green")
    public void the_option_i_selected_is_shown_as_green() {
        //Can't be tested with cucumber
    }

    @And("a Next Question button \\(could be an icon) is shown")
    public void a_next_question_button_could_be_an_icon_is_shown() throws Exception{
        resultActions.andExpect(content().string(
                org.hamcrest.Matchers.containsString(
                        "<form id=\"next-question\" action=\"/plant-guesser\" method=\"get\">"))
        )
        .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Next Question"))
        )
        .andExpect(content().string(
                org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("<form id=\"next-question\" hidden=\"hidden\" action=\"/plant-guesser\" method=\"get\">"))
        ));

    }

    @When("I click the Next Question button after guessing")
    public void i_click_the_next_question_button_after_guessing() throws Exception {
        i_select_the_correct_plant_name();
        plantImagePrevious = plantImage;
        quizOptionsPrevious = quizOptions;
        correctOptionPrevious = correctOption;
        roundNumberPrevious = roundNumber;
        i_go_to_the_plant_guesser_game_page();
    }

    @Then("I am shown a new image of a plant")
    public void i_am_shown_a_new_image_of_a_plant() {
        Assertions.assertNotEquals(plantImage, plantImagePrevious);
    }

    @And("I am shown four new options of plant names where one is the correct plant name and the other three are names of plants in the same family to click on")
    public void i_am_shown_four_new_options_of_plant_names_where_one_is_the_correct_plant_name_and_the_other_three_are_names_of_plants_in_the_same_family_to_click_on() {
        Assertions.assertNotEquals(quizOptionsPrevious.get(correctOption), quizOptions.get(correctOption));
    }

    @And("I see a text description saying Plant X+{int} {string}")
    public void i_see_a_text_description_saying_plant_X_1(int nextRound, String totalRounds) throws Exception {
        resultActions.andExpect(content().string(org.hamcrest.Matchers
                .containsString("Plant " + (roundNumberPrevious+nextRound) + totalRounds)));
    }

    @When("I select an incorrect plant name option")
    public void i_select_an_incorrect_plant_name_option() throws Exception {
        resultActions = mockMvc.perform(post("/plant-guesser")
                .param("selectedOption", String.valueOf(correctOption+1))
                .param("plantFamily", plantFamily)
                .param("quizOption1", quizOption1)
                .param("quizOption2", quizOption2)
                .param("quizOption3", quizOption3)
                .param("quizOption4", quizOption4)
                .param("plantImage", plantImage)
                .param("imageCredit", imageCredit)
                .param("roundNumber", String.valueOf(roundNumber))
                .param("correctOption", String.valueOf(correctOption))
                .param("score", String.valueOf(score)));

        mvcResult = resultActions.andExpect(status().isOk())
                .andReturn();
        get_model_data();

    }

    @Then("I am shown a message saying {string} correct plant name")
    public void i__am_shown_a_message_saying_correct_plant_name(String message) throws Exception{
        resultActions.andExpect(content().string(org.hamcrest.Matchers
                .containsString(message + plantNameJson)));
    }

    @And("the option I selected is shown as red")
    public void the_option_i_selected_is_shown_as_red() {
        //Can't be cucumber tested
    }

    @When("I have guessed for 10 plants \\(completed the game)")
    public void i_have_guessed_for_10_plants_completed_the_game() throws Exception{
        // round 1
        i_select_an_incorrect_plant_name_option();
        i_go_to_the_plant_guesser_game_page();
        // round 2
        i_select_the_correct_plant_name();
        i_go_to_the_plant_guesser_game_page();
        // round 3
        i_select_the_correct_plant_name();
        i_go_to_the_plant_guesser_game_page();
        // round 4
        i_select_an_incorrect_plant_name_option();
        i_go_to_the_plant_guesser_game_page();
        // round 5
        i_select_the_correct_plant_name();
        i_go_to_the_plant_guesser_game_page();
        // round 6
        i_select_the_correct_plant_name();
        i_go_to_the_plant_guesser_game_page();
        // round 7
        i_select_the_correct_plant_name();
        i_go_to_the_plant_guesser_game_page();
        // round 8
        i_select_an_incorrect_plant_name_option();
        i_go_to_the_plant_guesser_game_page();
        // round 9
        i_select_an_incorrect_plant_name_option();
        i_go_to_the_plant_guesser_game_page();
        // round 10
        i_select_the_correct_plant_name();
        i_go_to_the_plant_guesser_game_page();

    }

    @Then("an additional message is shown below any other messages {string}")
    public void an_additional_message_is_shown_below_any_other_messages(String message) throws Exception {
        resultActions.andExpect(content().string(org.hamcrest.Matchers
                .containsString(message)));
    }

    @And("I see my score of correct guesses out of 10")
    public void i_see_my_score_of_correct_guesses_out_of_10() throws Exception {
        resultActions.andExpect(content().string(org.hamcrest.Matchers
                .containsString("Total score: " + score + "/10")));
    }

    @And("my total Bloom count is updated and displayed")
    public void my_total_bloom_count_is_updated_and_displayed() {
        //not yet implemented
    }

    @When("I click the Back button \\(could be an icon)")
    public void i_click_the_back_button_could_be_an_icon() throws Exception {
        mvcResult = mockMvc.perform(get("/games"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Then("I am taken back to the Games page")
    public void i_am_taken_back_to_the_games_page() throws Exception {
        mvcResult = mockMvc.perform(get("/games"))
                .andExpect(status().isOk())
                .andReturn();
        String viewName = Objects.requireNonNull(mvcResult.getModelAndView()).getViewName();
        boolean onGamesPage = Objects.equals(viewName, "gamesTemplate");
        Assertions.assertTrue(onGamesPage);    }

    @And("my total Blooms are displayed")
    public void my_total_blooms_are_displayed() throws UnsupportedEncodingException {
        currentUser = userService.getAuthenticatedUser();
        Integer balance = currentUser.getBloomBalance();

        String content = mvcResult.getResponse().getContentAsString();  //repeated from bloom transaction step def but necessary as this is a different mvcResult

        boolean hasBloomBalance = content.contains("<div class=\"balanceDisplay\"")
                && content.contains("<span class=\"navBar-bloom-display\">" + balance.toString());

        Assertions.assertNotNull(currentUser.getBloomBalance(), "Expected bloom balance to be a number, but it was null");

        Assertions.assertTrue(hasBloomBalance, "Expected to find a bloom balance icon and number on the page.");
    }

    @And("my current game progress is not saved")
    public void my_current_game_progress_is_not_saved() {
        //not yet implemented
    }

    public void get_model_data() {
        modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        Map<String, Object> model = modelAndView.getModel();
        quizOptions = (List<String[]>) model.get("quizOptions");
        plantFamily = (String) model.get("plantFamily");
        quizOption1 = quizOptions.get(0)[0] + ',' + quizOptions.get(0)[1];
        quizOption2 = quizOptions.get(1)[0] + ',' + quizOptions.get(1)[1];
        quizOption3 = quizOptions.get(2)[0] + ',' + quizOptions.get(2)[1];
        quizOption4 = quizOptions.get(3)[0] + ',' + quizOptions.get(3)[1];
        plantImage = (String) model.get("plantImage");
        imageCredit = (String) model.get("imageCredit");
        roundNumber = (int) model.get("roundNumber");
        correctOption = (int) model.get("correctOption");
        score = (int) model.get("score");
    }
}
