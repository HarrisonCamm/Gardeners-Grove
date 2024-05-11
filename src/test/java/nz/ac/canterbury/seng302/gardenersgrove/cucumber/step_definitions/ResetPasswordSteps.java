package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.LostPasswordFormController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ResetPasswordFormController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.SignInController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.entity.VerificationToken;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.MailService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.VerificationTokenService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class ResetPasswordSteps {

    private static MockMvc mockMvcResetPassword;
    private static MockMvc mockMvcSignIn;
    private static MockMvc mockMvcLostPassword;

    @MockBean
    private static VerificationTokenService verificationTokenService;

    @MockBean
    private static AuthenticationManager authenticationManager;

    @MockBean
    private static UserService userService;

    @MockBean
    private static UserRepository userRepository;

    @MockBean
    private static MailService mailService;

    private static VerificationToken verificationToken;
    private String enteredPassword;
    private String reenteredPassword;
    private String enteredEmail;
    private ResultActions resultActions;

    @BeforeAll
    public static void before_or_after_all() {

        User loggedInUser = new User("user@gmail.com", "Test", "User", "p@ssw0rd123");

        // Mock the services and repositories
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        verificationTokenService = Mockito.mock(VerificationTokenService.class);
        mailService = Mockito.mock(MailService.class);
        userService = Mockito.mock(UserService.class);
        userRepository = Mockito.mock(UserRepository.class);

        LocalDateTime expiryDate = LocalDateTime.now().plusDays(1);

        verificationToken = new VerificationToken(loggedInUser, "123456", expiryDate);

        // Mock the verification token service
        when(verificationTokenService.findAllTokens()).thenReturn(List.of(verificationToken));
        when(verificationTokenService.validateToken(any(String.class))).thenReturn(true);
        when(verificationTokenService.getUserByToken(any(String.class))).thenReturn(loggedInUser);

        // Mock the user service
        when(userService.getAuthenicatedUser()).thenReturn(loggedInUser);

        //Mock the mail service
        doNothing().when(mailService).sendSimpleMessage(any(String.class), any(String.class), any(String.class));

        // Create for mockMvc
        ResetPasswordFormController resetPasswordFormController = new ResetPasswordFormController( userService, authenticationManager, verificationTokenService, mailService);
        SignInController signInController = new SignInController(userService, userRepository, authenticationManager, verificationTokenService);
        LostPasswordFormController lostPasswordFormController = new LostPasswordFormController(userService, authenticationManager, verificationTokenService, mailService);

        // Build the mockMVC
        mockMvcResetPassword = MockMvcBuilders.standaloneSetup(resetPasswordFormController).build();
        mockMvcSignIn = MockMvcBuilders.standaloneSetup(signInController).build();
        mockMvcLostPassword = MockMvcBuilders.standaloneSetup(lostPasswordFormController).build();

    }

    // AC1
    @WithMockUser
    @Given("I am on the login page")
    public void i_am_on_the_login_page() throws Exception {
        mockMvcSignIn.perform(get("/sign-in-form"))
                .andExpect(status().isOk())
                .andExpect(view().name("signInTemplate"));
    }

    //AC2, AC3
    @Given("I am on the lost password form")
    public void i_am_on_the_lost_password_form() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        mockMvcLostPassword.perform(get("/lost-password-form"))
                .andExpect(status().isOk())
                .andExpect(view().name("lostPasswordFormTemplate"));
    }

    //AC3
    @And("I enter a valid email that is not known to the system {string}")
    public void i_enter_a_valid_email_that_is_not_known_to_the_system(String email) {
        // Write code here that turns the phrase above into concrete actions
        this.enteredEmail = email;
    }

    //AC2
    @And("I enter an empty or malformed email address {string}")
    public void i_enter_an_empty_or_malformed_email_address(String email) {
        // Write code here that turns the phrase above into concrete actions
        this.enteredEmail = email;
    }

    //AC7
    @WithMockUser
    @Given("I am on the reset password form")
    public void i_am_on_the_reset_password_form() throws Exception {
        mockMvcResetPassword.perform(get("/reset-password-form")
                        .param("token", verificationToken.getToken()))
                .andExpect(status().isOk())
                .andExpect(view().name("resetPasswordFormTemplate"));
    }

    //AC1
    @WithMockUser
    @When("I hit the {string} link")
    public void i_Hit_the_link(String linkName) throws Exception { // Unused linkName
        resultActions = mockMvcLostPassword.perform(get("/lost-password-form"));
    }

    //AC2, AC3
    @When("I click the {string} button")
    public void i_click_the_button(String string) throws Exception {
        resultActions = mockMvcLostPassword.perform(post("/lost-password-form")
                .param("email", enteredEmail));
    }


    //AC7
    @WithMockUser
    @When("I enter the password {string}")
    public void i_enter_the_password(String password) {
        this.enteredPassword = password;
        this.reenteredPassword = password;
    }

    //AC7
    @WithMockUser
    @And("I hit the save button")
    public void i_hit_the_save_button() throws Exception {
        resultActions = mockMvcResetPassword.perform(post("/reset-password-form")
                .param("newPassword", enteredPassword)
                .param("retypedPassword", reenteredPassword)
                .param("token", verificationToken.getToken()));
    }

    //AC1
    @Then("I see a form asking me for my email address")
    public void i_see_a_form_asking_me_for_my_email_address() throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("lostPasswordFormTemplate"))
                .andExpect(model().attribute("email", "")); // The email field is empty
    }

    //AC2
    @Then("an error message tells me {string}")
    public void an_error_message_tells_me(String string) throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("lostPasswordFormTemplate"))
                .andExpect(model().attribute("lostPasswordEmailError", string));
    }

    //AC3
    @Then("a confirmation message tells me {string}")
    public void a_confirmation_message_tells_me(String string) throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("lostPasswordFormTemplate"))
                .andExpect(model().attribute("confirmationMessage", stringContainsInOrder(Collections.singletonList(string))));
    }


    //AC7
    @WithMockUser
    @Then("an error message tells {string}")
    public void an_error_message_tells(String message) throws Exception {
        resultActions
                .andExpect(status().isOk()) // No HTTP errors
                .andExpect(view().name("resetPasswordFormTemplate")) // We are on the same page with errors
                .andExpect(model().attribute("newPasswordError", message)); // Check that the model contains the expected error message
    }

}