package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.EditProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EditProfileController.class)
public class EditProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @BeforeEach
    public void setUp() {
        Mockito.reset(userService);
        User mockUser = new User("user@email.com", "User", "Name", "password");
        mockUser.grantAuthority("ROLE_USER");
//        mockUser.setUserId(1L);
        Mockito.when(userService.getAuthenicatedUser()).thenReturn(mockUser);
        Mockito.when(userService.updateUser(any(User.class), anyString(), anyString(), anyBoolean(), anyString(), anyString()))
                .thenAnswer(i -> ((User)i.getArgument(0)).setValues(i.getArgument(1), i.getArgument(2),
                        i.getArgument(3), i.getArgument(4), i.getArgument(5)));
//        Mockito.when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    /**
     * Test the successful alteration scenario where valid user data is submitted.
     */
    @Test
    @WithMockUser(username = "user@email.com", roles = {"USER"})
    public void whenPostEditProfileWithValidData_thenRedirectsToUserProfile() throws Exception {
        Mockito.when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(Mockito.mock(Authentication.class));
        mockMvc.perform(post("/edit-user-profile")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("dateOfBirth", "01/01/1990"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/view-user-profile"));

        verify(userService).updateUser(any(User.class), anyString(), anyString(), anyBoolean(), anyString(), anyString());
    }

    /**
     * Test errors behavior is correctly handled by the edit profile form when invalid data is submitted.
     * It simulates a POST request to the "/edit-user-profile" endpoint.
     */
    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "'', Doe, john.doe@example.com, 01/01/1990, firstNameError",
            "John, '', john.doe@example.com, 01/01/1990, lastNameError",
            "John, Doe, not-an-email, 01/01/1990, registrationEmailError",
            "John, Doe, john.doe@example.com, 01/01/1902, ageError",
    })
    void whenPostEditProfileWithInvalidData_thenReturnsFormViewWithErrors(String firstName, String lastName,
                                                                          String email, String dateOfBirth,
                                                                          String errorField) throws Exception {
        mockMvc.perform(post("/edit-user-profile")
                        .with(csrf())
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .param("email", email)
                        .param("dateOfBirth", dateOfBirth))
                .andExpect(status().isOk())
                .andExpect(view().name("editUserProfileTemplate"))
                .andExpect(model().attributeExists(errorField));
    }
}