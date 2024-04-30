package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
public class UserProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        Mockito.reset(userService, userRepository);
    }

    @Test
    public void whenGetUserProfileWithAuthenticatedUser_thenReturnsUserProfileView() throws Exception {
        User mockUser = new User("user@email.com", "User", "Name", "password");
        mockUser.setDateOfBirth("01/01/1990");

        when(userService.getAuthenicatedUser()).thenReturn(mockUser);

        mockMvc.perform(get("/view-user-profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewUserProfileTemplate"))
                .andExpect(model().attribute("displayName", "John Doe"))
                .andExpect(model().attribute("email", "john.doe@example.com"))
                .andExpect(model().attribute("dateOfBirth", "01/01/1990"));
    }
}