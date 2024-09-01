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
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class GamesPageSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private static MockMvc mockMvc;
    private MvcResult mvcResult;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @WithMockUser
    @Given("I am logged in")
    public void i_am_logged_in() throws Exception {
        mvcResult = mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainTemplate"))
                .andReturn();
    }

    @When("I click the 'Games' button on the navbar")
    public void i_click_the_games_button_on_the_navbar() throws Exception {
        mvcResult = mockMvc.perform(get("/games"))
                .andExpect(status().isOk())
                .andExpect(view().name("gamesTemplate"))
                .andReturn();
    }

    @Then("I should see the game {string} with a description")
    public void i_should_see_the_game_with_a_description(String gameTitle) throws Exception {
        String content = mvcResult.getResponse().getContentAsString();
        boolean hasGameTitle = content.contains("<h5 class=\"card-title\">" + gameTitle + "</h5>");
        boolean hasGameDescription = content.contains("Learn some plant names and images!");

        Assertions.assertAll(
                () -> Assertions.assertTrue(hasGameTitle, "Expected to find the '" + gameTitle + "' title on the page."),
                () -> Assertions.assertTrue(hasGameDescription, "Expected to find the description of the '" + gameTitle + "' game.")
        );
    }

    @Then("I should see a {string} button")
    public void i_should_see_a_button(String buttonText) throws Exception {
        String content = mvcResult.getResponse().getContentAsString();
        boolean hasPlayButton = content.contains("<a href=\"/games/play\" class=\"btn btn-primary\">" + buttonText + "</a>");

        Assertions.assertTrue(hasPlayButton, "Expected to find a '" + buttonText + "' button on the page.");
    }



    @Then("I am taken to the Games page")
    public void iAmTakenToTheGamesPage() {
        ModelAndView modelAndView = mvcResult.getModelAndView();
        String viewName = modelAndView.getViewName();

        Assertions.assertEquals("gamesTemplate", viewName, "Expected to be on the Games Page, but was not.");

        int status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(200, status, "Expected status 200 (OK), but got: " + status);
    }

}
