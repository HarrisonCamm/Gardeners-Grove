package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest
public class CreatePlantTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VerificationTokenService verificationTokenService;

    @MockBean
    private AuthorityService authorityService;

    @MockBean
    private MailService mailService;

    @MockBean
    private PlantService plantService;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AutocompleteService autocompleteService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private ImageService imageService;

    @MockBean
    private FriendRequestService friendRequestService;

    @MockBean
    private ModerationService moderationService;

    private Garden testGarden;
    private Location testLocation;
    private Plant testPlant;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User("user@email.com", "User", "Name", "password");
//        testUser.setUserId(1L);
        Mockito.when(userService.getAuthenicatedUser()).thenReturn(testUser);

        testLocation = new Location("123 Test Street", "Test Suburb", "Test City", "1234", "Test Country");
        testGarden = new Garden("Test Garden", testLocation, "1", testUser);
        testPlant = new Plant(testGarden, "Test Description");
    }

    @Test
    @WithMockUser
    public void GetPage_NoFields_Success() throws Exception {
        when(plantService.findPlant(any(Long.class))).thenReturn(Optional.ofNullable(testPlant));
        when(gardenService.findGarden(any(Long.class))).thenReturn(Optional.ofNullable(testGarden));

        mockMvc.perform(MockMvcRequestBuilders.get("/create-plant")
                .queryParam("gardenID", String.valueOf(1L))) // Any number will do
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser
    public void GetPage_NoFields_Failure() throws Exception {
        when(plantService.findPlant(any(Long.class))).thenReturn(Optional.ofNullable(testPlant));
        when(gardenService.findGarden(any(Long.class))).thenReturn(Optional.empty());


        mockMvc.perform(MockMvcRequestBuilders.get("/create-plant")
                .queryParam("gardenID", String.valueOf(1L))) // Any number will do
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "Tomato, " + Integer.MAX_VALUE + ", Yummy Tomato",
            "Potato, 2, Test Description",
    })
    public void PostForm_WithFields_Redirects(String name, String count, String description) throws Exception {
        when(plantService.findPlant(any(Long.class))).thenReturn(Optional.ofNullable(testPlant));
        when(gardenService.findGarden(any(Long.class))).thenReturn(Optional.ofNullable(testGarden));

        mockMvc.perform(MockMvcRequestBuilders.post("/create-plant")
                        .with(csrf())
                .queryParam("gardenID", String.valueOf(1L))
                .queryParam("datePlanted", "01/01/2021")
                .param("name", name)
                .param("count", count)
                .param("description", description))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        verify(plantService).addPlant(any(Plant.class));
    }

    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "Tomato, " + Integer.MIN_VALUE + ", Yummy Tomato",
            "Potato____, 0, Test Description",
            "Potato, -1, Test Description",
    })
    public void PostForm_WithInvalidFields_ErrorsShown(String name, String count, String description) throws Exception {
        when(plantService.findPlant(any(Long.class))).thenReturn(Optional.ofNullable(testPlant));
        when(gardenService.findGarden(any(Long.class))).thenReturn(Optional.ofNullable(testGarden));

        mockMvc.perform(MockMvcRequestBuilders.post("/create-plant")
                        .with(csrf())
                .queryParam("gardenID", String.valueOf(1L))
                .queryParam("datePlanted", "01/01/2021")
                .param("name", name)
                .param("count", count)
                .param("description", description))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        verify(plantService, Mockito.times(0)).addPlant(any(Plant.class));
    }
}
