package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.mock.web.MockHttpSession;

public class BloomTransactionSteps {

    @Before
    public void setUp() {
        mockSession = new MockHttpSession();
    }

    @Given("I am logged into the system")
    public void i_am_logged_into_the_system() {

        throw new io.cucumber.java.PendingException();
    }

    @When("I navigate to any page in the system")
    public void i_navigate_to_any_page_in_the_system() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see my Bloom balance displayed prominently in the header or a dedicated section")
    public void i_can_see_my_bloom_balance_displayed_prominently_in_the_header_or_a_dedicated_section() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }



}
