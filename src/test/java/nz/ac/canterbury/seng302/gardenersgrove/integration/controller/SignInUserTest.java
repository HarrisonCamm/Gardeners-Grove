package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.SignInController;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the RegisterFormController class.
 */
@WebMvcTest(SignInController.class)
public class SignInUserTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

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
