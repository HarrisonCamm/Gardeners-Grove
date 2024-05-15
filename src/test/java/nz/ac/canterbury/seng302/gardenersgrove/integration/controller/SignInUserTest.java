package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.SignInController;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the RegisterFormController class.
 */
@WebMvcTest(SignInController.class)
public class SignInUserTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private VerificationTokenService verificationTokenService;

    @MockBean
    private AuthorityService authorityService;

    @MockBean
    private MailService mailService;

    @MockBean
    private ImageService imageService;

    @BeforeEach
    public void setUp() {
        Mockito.reset();
    }

    /**
     * Test for accessing the sign-in form without any error
     */
    @Test
    @WithMockUser
    public void whenGetSignInForm_thenReturnsCorrectView() throws Exception {
        mockMvc.perform(get("/sign-in-form"))
                .andExpect(status().isOk())
                .andExpect(view().name("signInTemplate"))
                .andExpect(model().attributeExists("email"));
    }

    /**
     * Test for accessing the sign-in form with an error
     */
    @Test
    @WithMockUser
    public void whenGetSignInFormWithError_thenReturnsCorrectViewAndError() throws Exception {
        mockMvc.perform(get("/sign-in-form").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("signInTemplate"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attribute("signInError", "The email address is unknown, or the password is invalid"));
    }
}
