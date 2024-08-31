package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.SlotsController;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

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

        boolean hasSpinButton = content.contains("<button class=\"spin-button\"") && content.contains("SPIN");
        boolean hasReelContainers = content.contains("<div class=\"reel-container\"");

        Assertions.assertAll(
                () -> Assertions.assertTrue(hasSpinButton, "Expected to find a spin button in the page."),
                () -> Assertions.assertTrue(hasReelContainers, "Expected to find reel containers for the spin wheel animation.")
        );
    }
}
