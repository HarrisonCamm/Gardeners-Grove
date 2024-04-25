package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegisterFormController.class)
public class RegisterUserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @BeforeEach
    public void setUp() {
        Mockito.reset(userService);
    }

    // Blue sky test for successful registration
    @Test
    @WithMockUser
    public void whenPostRegisterFormWithValidData_thenRedirectsToUserProfile() throws Exception {
        // Mock the authentication process
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userService.emailExists("john.doe@example.com")).thenReturn(false);

        mockMvc.perform(post("/register-form")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("password", "Password@123")
                        .param("password2", "Password@123")
                        .param("dateOfBirth", "01/01/1990"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/view-user-profile"));

        // Verify that userService.addUser(...) was called
        verify(userService).addUser(any(User.class));
    }

    // Parameterized test for invalid input
    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "'', Doe, john.doe@example.com, Password@123, Password@123, 01/01/1990, firstNameError",
            "John, '', john.doe@example.com, Password@123, Password@123, 01/01/1990, lastNameError",
            "John, Doe, not-an-email, Password@123, Password@123, 01/01/1990, registrationEmailError",
            "John, Doe, john.doe@example.com, pass, pass, 01/01/1990, passwordValidityError",
            "John, Doe, john.doe@example.com, Password@123, DifferentPassword@123, 01/01/1990, passwordMatchError",
            "John, Doe, john.doe@example.com, Password@123, Password@123, 01/01/1902, ageError",
            // other test data
    })
    void whenPostRegisterFormWithInvalidData_thenReturnsFormViewWithErrors(String firstName, String lastName,
                                                                           String email, String password,
                                                                           String password2, String dateOfBirth,
                                                                           String errorField) throws Exception {
        mockMvc.perform(post("/register-form")
                        .with(csrf())
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .param("email", email)
                        .param("password", password)
                        .param("password2", password2)
                        .param("dateOfBirth", dateOfBirth))
                .andExpect(status().isOk())
                .andExpect(view().name("registerFormTemplate"))
                .andExpect(model().attributeExists(errorField));
    }

}
