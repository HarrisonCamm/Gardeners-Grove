package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class AuthenticationSteps {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Given("I am logged in with email {string} and password {string}")
    public void iAmLoggedInWithEmailTestGmailComAndPasswordHunter(String email, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        var authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
