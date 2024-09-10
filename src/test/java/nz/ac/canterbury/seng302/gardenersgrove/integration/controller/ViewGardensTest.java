package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.AutocompleteController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.MessagesController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Random;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest
public class ViewGardensTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private AutocompleteController autocompleteController;

    @MockBean
    private PlantService PlantService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

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

    @MockBean
    private FriendRequestService friendRequestService;

    @MockBean
    private TagService tagService;

    @MockBean
    private WeatherService weatherService;

    @MockBean
    private UserRelationshipService userRelationshipService;

    @MockBean
    private ModerationService moderationService;

    @MockBean
    private AlertService alertService;

    @MockBean
    private PlantGuesserService plantGuesserService;

    @MockBean
    private PlantFamilyService plantFamilyService;

    @MockBean
    private MessagesController messagesController;

    @MockBean
    private Random random;

    private User testUser;

    @BeforeEach
    public void setup() {
        testUser = new User("user@email.com", "User", "Name", "password");
//        testUser.setUserId(1L);
        Mockito.when(userService.getAuthenticatedUser()).thenReturn(testUser);
    }

    @Test
    @WithMockUser(username = "user@email.com")
    public void GetPage_FromAnywhere_Ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/view-gardens"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
