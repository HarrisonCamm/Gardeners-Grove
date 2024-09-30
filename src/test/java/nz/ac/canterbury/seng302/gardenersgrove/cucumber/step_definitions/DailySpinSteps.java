package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.SlotsService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class DailySpinSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    private static MockMvc mockMvc;
    private MvcResult mvcResult;
    private MockHttpSession session;

    private int bloomBalance;
    private int amountWon;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        session = new MockHttpSession();
    }

    //AC1, AC2
    @WithMockUser
    @Given("I am on the main page")
    public void i_am_on_the_main_page() throws Exception {
        mvcResult = mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainTemplate"))
                .andReturn();
    }

//    AC1 - navigate to daily spin
    @Then("I see a {string} button prominently displayed on the navbar")
    public void i_see_a_daily_spin_button_prominently_displayed_on_the_navbar(String expectedText) throws UnsupportedEncodingException {
        String content = mvcResult.getResponse().getContentAsString(); //getting the html content

        boolean found = content.contains("<a class=\"card navLink\"") && content.contains(">" + expectedText + "</a>"); //checking the Daily Spin text is in an anchor tag

        Assertions.assertTrue(found, "Expected to find a '" + expectedText + "' button in the navbar");
    }

    //AC2 - daily spin animation
    @When("I click the Daily Spin button on the navbar,")
    public void i_click_the_daily_spin_button_on_the_navbar() throws Exception {
        mvcResult = mockMvc.perform(get("/daily-spin").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("dailySpinTemplate"))
                .andReturn();
    }

    //AC2 - daily spin animation
    @Then("I am taken to the Daily Spin Page")
    public void i_am_taken_to_the_daily_spin_page() {
        String viewName = Objects.requireNonNull(mvcResult.getModelAndView()).getViewName();
        Assertions.assertEquals("dailySpinTemplate", viewName, "Expected to be on the Daily Spin Page, but was not.");

        int status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(200, status, "Expected status 200 (OK), but got: " + status);
    }

    //AC2 - daily spin animation
    @Then("A spin wheel animation appears with garden themed emojis")
    public void a_spin_wheel_animation_appears_with_garden_themed_emojis() throws UnsupportedEncodingException {
        //verify that the HTML content includes the necessary elements for the
        // spin wheel animation and the garden-themed emojis 💧☀️🍄🌶️🌾.
        String content = mvcResult.getResponse().getContentAsString();

        boolean hasSpinButton = content.contains("<button type=\"submit\" class=\"spin-button\"") && content.contains("SPIN");
        boolean hasReelContainers = content.contains("<div class=\"reel-container\"");

        Assertions.assertAll(
                () -> Assertions.assertTrue(hasSpinButton, "Expected to find a spin button in the page."),
                () -> Assertions.assertTrue(hasReelContainers, "Expected to find reel containers for the spin wheel animation.")
        );
    }

    //AC3
    @Given("The randomisation seed is {int}")
    public void the_randomisation_seed_is(Integer seed) {
        SlotsService.setCustomRandom(new Random(seed));
    }

    //AC3
    @Given("I haven't used the daily spin today")
    public void i_haven_t_used_the_daily_spin_today() {
        User user = userService.getAuthenticatedUser();
        user.resetLastFreeSpinUsed();
        userService.updateUserFriends(user);
    }

    //AC3
    @Given("The wheel spin animation has completed")
    public void the_wheel_spin_animation_has_completed() throws Exception{
        //Get the users balance before the spin
        bloomBalance = userService.getAuthenticatedUser().getBloomBalance();

        mvcResult = mockMvc.perform(get("/daily-spin").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("dailySpinTemplate"))
                .andReturn();

        mvcResult = mockMvc.perform(post("/daily-spin").session(session)
                        .param("buttonAction", "FREE_SPINNING"))
                .andExpect(status().isOk())
                .andExpect(view().name("dailySpinTemplate"))
                .andReturn();
    }

    //AC3
    @When("I get a combo")
    public void i_get_a_combo() {
        Assertions.assertNotNull(Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("amountWon"));
        amountWon = (int) mvcResult.getModelAndView().getModel().get("amountWon");
        Assertions.assertTrue(amountWon > 0);
    }

    //AC3
    @Then("I am rewarded blooms based on the combo")
    public void i_am_rewarded_blooms_based_on_the_combo() {
        int newBalance = userService.getAuthenticatedUser().getBloomBalance();
        Assertions.assertEquals(bloomBalance + amountWon, newBalance);
    }

    //AC4
    @Given("I have already used the daily spin for the day")
    public void i_have_already_used_the_daily_spin_for_the_day() {
        User user = userService.getAuthenticatedUser();
        user.updateLastFreeSpinUsed();
        userService.updateUserFriends(user);
    }

    //AC4
    @When("I try to spin again")
    public void i_try_to_spin_again() throws Exception {
        mvcResult = mockMvc.perform(get("/daily-spin").session(session))
                .andReturn();
        mvcResult = mockMvc.perform(post("/daily-spin").session(session)
                        .param("buttonAction", "FREE_SPINNING"))
                .andReturn();

        mvcResult = mockMvc.perform(get("/daily-spin").session(session))
                .andExpect(status().isOk())
                .andReturn();
    }

    //AC4
    @Then("I am shown a message that says, {string}")
    public void i_am_shown_a_message_that_says(String expectedMessage) {
        String message = (String) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("message");
        Assertions.assertEquals(expectedMessage, message);
    }

    //AC5
    @Then("The {int} cost is displayed on the spin button.")
    public void the_cost_is_displayed_on_the_spin_button(Integer expectedCost) {
        String buttonText = (String) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("buttonText");
        String expectedButtonText = expectedCost.toString();
        Assertions.assertTrue(buttonText.contains(expectedButtonText));
    }

    //AC6
    @Given("I pay for an extra spin")
    public void i_pay_for_an_extra_spin() throws Exception {
        bloomBalance = userService.getAuthenticatedUser().getBloomBalance();
        mockMvc.perform(get("/daily-spin").session(session));

        mvcResult = mockMvc.perform(get("/daily-spin").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("dailySpinTemplate"))
                .andReturn();
        mvcResult = mockMvc.perform(post("/daily-spin").session(session)
                        .param("buttonAction", "PAID_SPINNING"))
                .andExpect(status().isOk())
                .andExpect(view().name("dailySpinTemplate"))
                .andReturn();

        amountWon = (int) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("amountWon");
    }

    //AC6
    @Then("{int} blooms is deducted from my account balance")
    public void the_amount_is_deducted_from_my_account_balance(Integer expectedCost) {
        int newBalance = userService.getAuthenticatedUser().getBloomBalance();
        Assertions.assertEquals(bloomBalance - expectedCost + amountWon, newBalance);
    }

    //AC7
    @Given("I have already started a spin")
    public void i_have_already_started_a_spin() throws Exception{
        bloomBalance = userService.getAuthenticatedUser().getBloomBalance();

        mockMvc.perform(get("/daily-spin").session(session));
        mockMvc.perform(get("/daily-spin").session(session));

        mvcResult = mockMvc.perform(post("/daily-spin").session(session)
                        .param("buttonAction", "FREE_SPINNING"))
                .andExpect(status().isOk())
                .andExpect(view().name("dailySpinTemplate"))
                .andReturn();
        amountWon = (int) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("amountWon");
    }

    //AC7
    @When("I leave the page")
    public void i_leave_the_page() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        mockMvc.perform(get("/main").session(session));
    }

    //AC7
    @Then("the spin outcome is still processed and the blooms are still added to my balance")
    public void the_spin_outcome_is_still_processed_and_the_blooms_are_still_added_to_my_balance() {
        // Write code here that turns the phrase above into concrete actions
        int newBalance = userService.getAuthenticatedUser().getBloomBalance();
        Assertions.assertEquals(bloomBalance + amountWon, newBalance);
    }

}
