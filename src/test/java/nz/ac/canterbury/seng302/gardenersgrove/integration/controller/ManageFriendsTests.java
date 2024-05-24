package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.ManageFriendsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ManageFriendsController.class)
public class ManageFriendsTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private static FriendRequestService friendRequestService;

    @MockBean
    private static UserService userService;

    @MockBean
    private MailService mailService;

    @MockBean
    private ImageService imageService;

    private static User loggedUser;
    private static User testUser;

    @BeforeEach
    public void setUp() {
        loggedUser = new User("logged@email.com", "foo", "bar", "password");
        testUser = new User("test@email.com", "test", "user", "password");

        when(userService.getAuthenicatedUser()).thenReturn(loggedUser);
        when(userService.searchForUsers(any(String.class))).thenReturn(List.of(testUser));
        when(userService.getUserByEmail(any(String.class))).thenReturn(testUser);

        doNothing().when(friendRequestService).sendRequest(loggedUser, testUser);
    }

    @WithMockUser
    @Test
    public void OnAnywhere_GetPage_IsOkay() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/manage-friends"))
                .andExpect(status().isOk())
                .andExpect(view().name("manageFriendsTemplate"))
                .andExpect(model().attribute("showSearch", true));
    }

    @WithMockUser
    @Test
    public void OnManageFriends_SearchForUser_IsOkay() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/manage-friends")
                .with(csrf())
                    .param("action", "search")
                    .param("searchQuery", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("manageFriendsTemplate"))
                .andExpect(model().attribute("showSearch", true))
                .andExpect(model().attribute("matchedUsers", List.of(testUser)));
    }

    @WithMockUser
    @Test
    public void OnManageFriends_InviteUser_IsOkay() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/manage-friends")
                        .with(csrf())
                        .param("action", "invite")
                        .param("email", testUser.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("manageFriendsTemplate"));

        verify(friendRequestService, times(1)).sendRequest(loggedUser, testUser);
    }
}
