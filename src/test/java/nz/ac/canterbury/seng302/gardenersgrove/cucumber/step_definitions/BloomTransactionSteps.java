package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.VerificationTokenService;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class BloomTransactionSteps {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    UserService userService;

    private MockMvc mockMvc;
    private MvcResult mvcResult;

    private User currentUser;
    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    //AC1
    @When("I navigate to any page {string} in the system")
    public void i_navigate_to_any_page_in_the_system(String endpoint) throws Exception {
        mvcResult = mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andReturn();
    }

    //AC1
    @Then("I can see my Bloom balance displayed prominently in the header or a dedicated section")
    public void i_can_see_my_bloom_balance_displayed_prominently_in_the_header_or_a_dedicated_section() throws UnsupportedEncodingException {

        currentUser = userService.getAuthenticatedUser();
        Integer balance = currentUser.getBloomBalance();

        String content = mvcResult.getResponse().getContentAsString();

        boolean hasBloomBalance = content.contains("<div class=\"balanceDisplay\"")
                && content.contains("<span class=\"navBar-bloom-display\">" + balance.toString());

        Assertions.assertNotNull(currentUser.getBloomBalance(), "Expected bloom balance to be a number, but it was null");

        Assertions.assertTrue(hasBloomBalance, "Expected to find a bloom balance icon and number on the page.");

    }

    @Given("I am logged into the system")
    public void i_am_logged_into_the_system() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("I navigate to my profile page")
    public void i_navigate_to_my_profile_page() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("I can see my current Bloom balance displayed prominently")
    public void i_can_see_my_current_bloom_balance_displayed_prominently() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("I can see a detailed transaction history for the Bloom currency")
    public void i_can_see_a_detailed_transaction_history_for_the_bloom_currency() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("the transaction history should be paginated or scrollable if it exceeds a certain number of entries")
    public void the_transaction_history_should_be_paginated_or_scrollable_if_it_exceeds_a_certain_number_of_entries() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


}
