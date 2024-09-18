package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class LeaderboardSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private static MockMvc mockMvc;
    private MvcResult mvcResult;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }



    @Then("I can see a table with the ten users of the app who have the highest number of blooms")
    public void i_can_see_a_table_with_top_ten_users() throws Exception {
        mvcResult = mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainTemplate"))
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("<table class=\"leaderboard-table\">"), "Expected to find a table element");
    }

    @Then("I can see three columns in the leaderboard table")
    public void i_can_see_three_columns_in_the_leaderboard_table() throws Exception {
        mvcResult = mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainTemplate"))
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("<th>Ranking</th>"), "Expected a 'Ranking' column");
        Assertions.assertTrue(content.contains("<th>Profile & Name</th>"), "Expected a 'Profile & Name' column");
    }

    @Then("the first column is the rank")
    public void the_first_column_is_the_rank() throws Exception {
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("<th>Ranking</th>"), "Expected the first column to be 'Ranking'");
    }

    @Then("the second column is the profile picture and name")
    public void the_second_column_is_the_profile_picture_and_name() throws Exception {
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("<th>Profile & Name</th>"), "Expected the second column to be 'Profile & Name'");
    }

    @Then("the third column is the number of blooms")
    public void the_third_column_is_the_blooms() throws Exception {
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("<div class=\"svg\">"), "Expected the third column to be 'Blooms'");
    }

    @Then("I can see my placing on the table at the bottom with my ranking out of all the users")
    public void i_can_see_my_placing_on_the_table_at_the_bottom() throws Exception {
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertFalse(content.contains("Your Rank:"), "Expected the current user placement to be displayed outside the top 10");
    }

    @And("I am not in the top 10, when I view the leaderboard table")
    public void iAmNotInTheTopWhenIViewTheLeaderboardTable() throws Exception {
        mvcResult = mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainTemplate"))
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertFalse(content.contains("Your Rank:"), "Expected the current user placement to be displayed outside the top 10");
    }
}
