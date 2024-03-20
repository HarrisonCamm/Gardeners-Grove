//package nz.ac.canterbury.seng302.gardenersgrove.controller;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
//
//import nz.ac.canterbury.seng302.gardenersgrove.controller.RegisterFormController;
//import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
//import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//public class RegisterUserIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private RegisterFormController registerFormController;
//
//    @MockBean
//    private UserService userService;
//
//    @MockBean
//    private AuthenticationManager authenticationManager;
//
//    // Blue sky test for successful registration
//    @Test
//    @WithMockUser
//    void whenPostRegisterFormWithValidData_thenRedirectsToUserProfile() throws Exception {
//        // Mock the authentication process
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(mock(Authentication.class));
//        when(userService.emailExists("john.doe@example.com")).thenReturn(false);
//
//        mockMvc.perform(post("/register-form")
//                        .param("firstName", "John")
//                        .param("lastName", "Doe")
//                        .param("email", "john.doe@example.com")
//                        .param("password", "Password@123")
//                        .param("password2", "Password@123")
//                        .param("dateOfBirth", "1990-01-01"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/view-user-profile"));
//
//        // Verify that userService.addUser(...) was called
//        verify(userService).addUser(any(User.class));
//    }
//
//    // Parameterized test for invalid input
//    @ParameterizedTest
//    @CsvSource({
//            "'', Doe, john.doe@example.com, Password@123, Password@123, 1990-01-01, firstNameError",
//            "John, '', john.doe@example.com, Password@123, Password@123, 1990-01-01, lastNameError",
//            "John, Doe, not-an-email, Password@123, Password@123, 1990-01-01, registrationEmailError",
//            "John, Doe, john.doe@example.com, pass, pass, 1990-01-01, passwordValidityError",
//            "John, Doe, john.doe@example.com, Password@123, DifferentPassword@123, 1990-01-01, passwordMatchError",
//            "John, Doe, john.doe@example.com, Password@123, Password@123, 2010-01-01, ageError",
//            // other test data
//    })
//    void whenPostRegisterFormWithInvalidData_thenReturnsFormViewWithErrors(String firstName, String lastName,
//                                                                           String email, String password,
//                                                                           String password2, String dateOfBirth,
//                                                                           String errorField) throws Exception {
//        mockMvc.perform(post("/register-form")
//                        .param("firstName", firstName)
//                        .param("lastName", lastName)
//                        .param("email", email)
//                        .param("password", password)
//                        .param("password2", password2)
//                        .param("dateOfBirth", dateOfBirth))
//                .andExpect(status().isOk())
//                .andExpect(view().name("registerFormTemplate"))
//                .andExpect(model().attributeExists(errorField));
//    }
//
//}