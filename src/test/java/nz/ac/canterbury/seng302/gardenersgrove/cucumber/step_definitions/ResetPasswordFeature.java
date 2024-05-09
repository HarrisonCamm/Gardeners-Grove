package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ResetPasswordFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.entity.VerificationToken;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.VerificationTokenService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ResetPasswordFeature.class)
@AutoConfigureMockMvc
public class ResetPasswordFeature {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private static VerificationTokenService verificationTokenService;

    @MockBean
    private static UserService userService;

    private static User loggedInUser;

    private static VerificationToken verificationToken;

    @BeforeAll
    public static void before_or_after_all() {
        // This acts as our user that is logged in
        loggedInUser = new User("user@gmail.com", "Test", "User", "p@ssw0rd123");

        userService = Mockito.mock(UserService.class);
        verificationTokenService = Mockito.mock(VerificationTokenService.class);

        //Mock the getAuthenticatedUser method to return the loggedInUser
        when(userService.getAuthenicatedUser()).thenReturn(loggedInUser);

        // Trivial example of an expiry date
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(1);

        verificationToken = new VerificationToken(loggedInUser, "123456", expiryDate);

        // Return the token when the token is requested and return the user when any token is used
        when(verificationTokenService.findAllTokens()).thenReturn(List.of(verificationToken));
        when(verificationTokenService.getUserByToken(any(String.class))).thenReturn(loggedInUser);
    }

    @WithMockUser
    @Given("I am on the reset password form")
    public void i_am_on_the_reset_password_form() throws Exception {
        mockMvc.perform(get("/reset-password-form")
                        .param("token", verificationToken.getToken()))
                .andExpect(status().isOk())
                .andExpect(view().name("resetPasswordFormTemplate"));
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