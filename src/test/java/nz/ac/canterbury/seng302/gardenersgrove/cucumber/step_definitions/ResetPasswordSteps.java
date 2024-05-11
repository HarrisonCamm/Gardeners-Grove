package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ResetPasswordFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.entity.VerificationToken;
import nz.ac.canterbury.seng302.gardenersgrove.service.MailService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.VerificationTokenService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class ResetPasswordSteps {

    private static MockMvc mockMvc;

    @MockBean
    private static VerificationTokenService verificationTokenService;

    @MockBean
    private static AuthenticationManager authenticationManager;

    @MockBean
    private static UserService userService;

    @MockBean
    private static MailService mailService;

    private static User loggedInUser;

    private static VerificationToken verificationToken;
    private String enteredPassword;
    private String reenteredPassword;
    private ResultActions resultActions;

    @BeforeAll
    public static void before_or_after_all() {

        loggedInUser = new User("user@gmail.com", "Test", "User", "p@ssw0rd123");

        authenticationManager = Mockito.mock(AuthenticationManager.class);
        verificationTokenService = Mockito.mock(VerificationTokenService.class);

        LocalDateTime expiryDate = LocalDateTime.now().plusDays(1);

        verificationToken = new VerificationToken(loggedInUser, "123456", expiryDate);

        when(verificationTokenService.findAllTokens()).thenReturn(List.of(verificationToken));
        when(verificationTokenService.validateToken(any(String.class))).thenReturn(true);

        when(verificationTokenService.getUserByToken(any(String.class))).thenReturn(loggedInUser);

        mailService = Mockito.mock(MailService.class);
        userService = Mockito.mock(UserService.class);

        ResetPasswordFormController resetPasswordFormController = new ResetPasswordFormController( userService, authenticationManager, verificationTokenService, mailService);

        mockMvc = MockMvcBuilders.standaloneSetup(resetPasswordFormController).build();

        userService = Mockito.mock(UserService.class);

        when(userService.getAuthenicatedUser()).thenReturn(loggedInUser);

    }

    @WithMockUser
    @Given("I am on the reset password form")
    public void i_am_on_the_reset_password_form() throws Exception {
        mockMvc.perform(get("/reset-password-form")
                        .param("token", verificationToken.getToken()))
                .andExpect(status().isOk())
                .andExpect(view().name("resetPasswordFormTemplate"));
    }

    @WithMockUser
    @When("I enter the password {string}")
    public void i_enter_the_password(String password) {
        this.enteredPassword = password;
        this.reenteredPassword = password;
    }

    @WithMockUser
    @And("I hit the save button")
    public void i_hit_the_save_button() throws Exception {
        resultActions = mockMvc.perform(post("/reset-password-form")
                .param("newPassword", enteredPassword)
                .param("retypedPassword", reenteredPassword)
                .param("token", verificationToken.getToken()));
    }

    @WithMockUser
    @Then("an error message tells {string}")
    public void an_error_message_tells(String message) throws Exception {
        resultActions
                .andExpect(status().isOk()) // No HTTP errors
                .andExpect(view().name("resetPasswordFormTemplate")) // We are on the same page with errors
                .andExpect(model().attribute("newPasswordError", message)); // Check that the model contains the expected error message
    }
}