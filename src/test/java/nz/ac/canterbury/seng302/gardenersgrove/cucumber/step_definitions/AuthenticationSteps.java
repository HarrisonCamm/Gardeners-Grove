package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

public class AuthenticationSteps {

    @Given("I am logged in with email {string} and password {string}")
    public void iAmLoggedInWithEmailTestGmailComAndPasswordHunter(String email, String password) {
        var authentication = new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
