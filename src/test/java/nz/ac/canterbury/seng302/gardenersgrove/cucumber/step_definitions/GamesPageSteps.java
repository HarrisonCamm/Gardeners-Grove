package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
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

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Given("I am logged in")
    public void iAmLoggedIn() throws Exception {
        MvcResult loginResult = mockMvc.perform(get("/main")
                        .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("mainTemplate"))
                .andReturn();
    }

    @When("I click the 'Games' button on the navbar")
    public void iClickTheGamesButtonOnTheNavbar() throws Exception {
        MvcResult gamesResult = mockMvc.perform(get("/games")
                        .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("gamesTemplate"))
                .andReturn();
    }

    @Then("I should see the game {string} with a description")
    public void iShouldSeeTheGameWithADescription(String gameTitle) throws Exception {
        MvcResult gamesResult = mockMvc.perform(get("/games")
                        .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();

        String content = gamesResult.getResponse().getContentAsString();
        boolean hasGameTitle = content.contains("<h5 class=\"card-title\">" + gameTitle + "</h5>");
        boolean hasGameDescription = content.contains("Learn some plant names and images!");

        Assertions.assertAll(
                () -> Assertions.assertTrue(hasGameTitle, "Expected to find the '" + gameTitle + "' title on the page."),
                () -> Assertions.assertTrue(hasGameDescription, "Expected to find the description of the '" + gameTitle + "' game.")
        );
    }

    @Then("I should see a {string} button")
    public void iShouldSeeAButton(String buttonText) throws Exception {
        MvcResult gamesResult = mockMvc.perform(get("/games")
                        .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();

        String content = gamesResult.getResponse().getContentAsString();
        boolean hasPlayButton = content.contains("<a href=\"/daily-spin\" class=\"btn btn-primary\">" + buttonText + "</a>");

        Assertions.assertTrue(hasPlayButton, "Expected to find a '" + buttonText + "' button on the page.");
    }

    @Then("I am taken to the Games page")
    public void iAmTakenToTheGamesPage() throws Exception {
        MvcResult gamesResult = mockMvc.perform(get("/games")
                        .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = gamesResult.getModelAndView();
        String viewName = modelAndView.getViewName();

        Assertions.assertEquals("gamesTemplate", viewName, "Expected to be on the Games Page, but was not.");

        int status = gamesResult.getResponse().getStatus();
        Assertions.assertEquals(200, status, "Expected status 200 (OK), but got: " + status);
    }
}
