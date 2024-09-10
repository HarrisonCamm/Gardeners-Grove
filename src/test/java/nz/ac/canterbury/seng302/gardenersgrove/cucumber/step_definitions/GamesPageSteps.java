package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
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

    private MockMvc mockMvc;
    private ResultActions resultActions;
    private MvcResult mvcResult;
    private ModelAndView modelAndView;

    private String gameName;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @When("I click the 'Games' button on the navbar")
    public void iClickTheGamesButtonOnTheNavbar() throws Exception {
        mvcResult = mockMvc.perform(get("/games")
                        .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("gamesTemplate"))
                .andReturn();
    }

    @Then("I should see the game {string} with a description")
    public void iShouldSeeTheGameWithADescription(String gameTitle) throws Exception {
        mvcResult = mockMvc.perform(get("/games")
                        .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        boolean hasGameTitle = content.contains("<h5 class=\"card-title\">" + gameTitle + "</h5>");
        boolean hasGameDescription = content.contains("Learn some plant names and images!");

        Assertions.assertAll(
                () -> Assertions.assertTrue(hasGameTitle, "Expected to find the '" + gameTitle + "' title on the page."),
                () -> Assertions.assertTrue(hasGameDescription, "Expected to find the description of the '" + gameTitle + "' game.")
        );
    }

    @Then("I should see a {string} button")
    public void iShouldSeeAButton(String buttonText) throws Exception {
        mvcResult = mockMvc.perform(get("/games")
                        .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        boolean hasPlayButton = content.matches("(?s).*<a.*class=\"btn btn-primary\".*>\\s*" + buttonText + "\\s*</a>.*");

        Assertions.assertTrue(hasPlayButton, "Expected to find a '" + buttonText + "' button on the page.");
    }



    @Then("I am taken to the Games page")
    public void iAmTakenToTheGamesPage() throws Exception {
        mvcResult = mockMvc.perform(get("/games")
                        .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        String viewName = modelAndView.getViewName();

        Assertions.assertEquals("gamesTemplate", viewName, "Expected to be on the Games Page, but was not.");

        int status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(200, status, "Expected status 200 (OK), but got: " + status);
    }

    @When("I click the Play button for a game {string}")
    public void i_click_the_play_button_for_a_game(String game) throws Exception {
        resultActions = mockMvc.perform(get(game));
        if (game.equals("/plant-guesser")) {
            gameName = "plantGuesserTemplate";
        } else if (game.equals("/daily-spin")) {
            gameName = "dailySpinTemplate";
        }
    }

    @Then("I am taken to a page displaying the game to play")
    public void i_am_taken_to_a_page_displaying_the_game_to_play() throws Exception {
        mvcResult = resultActions.andExpect(status().isOk())
                .andReturn();
        modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        String viewName = modelAndView.getViewName();
        Assertions.assertEquals(gameName, viewName);

    }
}
