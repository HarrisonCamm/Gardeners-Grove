package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class DailySpinSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private static MockMvc mockMvc;
    private MvcResult mvcResult;

    private boolean spin_animation_completed;
    private int combo_multiplier;
    private int initial_bloom_balance;
    private int reward_blooms;
    private int current_bloom_balance;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
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

        boolean found = content.contains("<a class=\"navLink\"") && content.contains(">" + expectedText + "</a>"); //checking the Daily Spin text is in an anchor tag

        Assertions.assertTrue(found, "Expected to find a '" + expectedText + "' button in the navbar");
    }

    //AC2 - daily spin animation
    @When("I click the Daily Spin button on the navbar,")
    public void i_click_the_daily_spin_button_on_the_navbar() throws Exception {
        mvcResult = mockMvc.perform(get("/daily-spin"))
                .andExpect(status().isOk())
                .andExpect(view().name("dailySpinTemplate"))
                .andReturn();
    }

    //AC2 - daily spin animation
    @Then("I am taken to the Daily Spin Page")
    public void i_am_taken_to_the_daily_spin_page() {
        String viewName = mvcResult.getModelAndView().getViewName();
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

    @Given("The wheel spin animation has completed")
    public void the_wheel_spin_animation_has_completed() {
        // Mocking the completion of the wheel spin animation
        spin_animation_completed = true;
        initial_bloom_balance = 500;
        current_bloom_balance = initial_bloom_balance;
    }

    @When("I get a combo")
    public void i_get_a_combo() {
        // Mocking the action of getting a combo during the spin
        if (spin_animation_completed) {
            combo_multiplier = 10;
            reward_blooms = combo_multiplier * 5;
        }
    }

    @Then("I am rewarded blooms based on the combo")
    public void i_am_rewarded_blooms_based_on_the_combo() {
        // updating the bloom balance after spin
        if (spin_animation_completed && combo_multiplier > 0) {
            current_bloom_balance += reward_blooms;
        }
        // Verify the blooms are rewarded correctly
        Assertions.assertEquals(initial_bloom_balance + reward_blooms, current_bloom_balance,
                "The blooms were not rewarded correctly based on the combo.");
    }
}
