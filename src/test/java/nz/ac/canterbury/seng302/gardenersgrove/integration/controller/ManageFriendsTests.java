package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.ManageFriendsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.FriendRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ShopRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private ItemService itemService;

    @MockBean
    private ShopService shopService;

    @MockBean
    private ShopRepository shopRepository;

    @MockBean
    private ImageService imageService;

    @MockBean
    private UserRelationshipService userRelationshipService;

    private static User loggedUser;
    private static User testUser;

    @BeforeEach
    public void setUp() {
        loggedUser = new User("logged@email.com", "foo", "bar", "password");
        testUser = new User("test@email.com", "test", "user", "password");

        FriendRequest friendRequest = new FriendRequest(loggedUser, testUser);

        when(userService.getAuthenticatedUser()).thenReturn(loggedUser);
        when(userService.searchForUsers(any(String.class), any(User.class))).thenReturn(List.of(testUser));
        when(userService.getUserByEmail(any(String.class))).thenReturn(testUser);
        when(userService.getSentFriendRequests(any(User.class))).thenReturn(List.of(friendRequest));

        doNothing().when(friendRequestService).save(friendRequest);
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
                .andExpect(model().attribute("showSearch", false));
    }

    @WithMockUser
    @Test
    public void OnManageFriends_InviteUser_IsOkay() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/manage-friends")
                .with(csrf())
                    .param("action", "invite")
                    .param("email", testUser.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/manage-friends"));
    }

    @WithMockUser
    @Test
    public void OnManageFriends_CancelInvite_IsOkay() throws Exception {

        List <FriendRequest> emptyList = List.of();
        when(userService.getSentFriendRequests(any(User.class))).thenReturn(emptyList);

        mockMvc.perform(MockMvcRequestBuilders.post("/manage-friends")
                        .with(csrf())
                        .param("action", "cancel")
                        .param("email", testUser.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/manage-friends"));
    }

    @WithMockUser
    @Test
    public void OnManageFriends_AcceptFriendRequest_IsOkay() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/manage-friends")
                        .with(csrf())
                        .param("action", "accept")
                        .param("email", testUser.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/manage-friends"));
    }

    @WithMockUser
    @Test
    public void OnManageFriends_DenyFriendRequest_IsOkay() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/manage-friends")
                        .with(csrf())
                        .param("action", "delete")
                        .param("email", testUser.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/manage-friends"));
    }

    @WithMockUser
    @Test
    public void CanceledRequest_AcceptRequest_DoesNotSave() throws Exception {
        friendRequestService.save(new FriendRequest(testUser, loggedUser));

        // Immediately cancel the request
        friendRequestService.cancelRequest(testUser, loggedUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/manage-friends")
                        .with(csrf())
                        .param("action", "accept")
                        .param("email", testUser.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/manage-friends"));


        assertTrue(userService.getAuthenticatedUser().getFriends().isEmpty());

    }
}
