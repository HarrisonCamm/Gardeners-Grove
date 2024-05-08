package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ResetPasswordFeature {

    @Given("I am on the reset password form")
    public void i_am_on_the_reset_password_form() {
        // code to navigate to the reset password form
    }

    @When("I enter a weak password")
    public void i_enter_a_weak_password() {
        // code to enter a weak password
    }

    @When("I hit the save button")
    public void i_hit_the_save_button() {
        // code to hit the save button
    }

    @Then("an error message tells {string}")
    public void an_error_message_tells(String message) {
        // code to check the error message
    }
}