package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.CreateGardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ShopRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(CreateGardenController.class)
public class CreateGardenTests {

    private static final String SURFACE_AREA_OF_EARTH = "510100000";


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ShopRepository shopRepository;

    @MockBean
    private ShopService shopService;

    @MockBean
    LocationService locationService;

    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private ImageService imageService;

    @MockBean
    private ModerationService moderationService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User("user@email.com", "User", "Name", "password");
        Mockito.when(userService.getAuthenticatedUser()).thenReturn(testUser);
        when(moderationService.isContentAppropriate(null)).thenReturn(true);
    }

    @Test
    @WithMockUser
    public void RequestPage_NoFields_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/create-garden"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "Lovely Garden, 12.00009123",
            "Tomato's, " + SURFACE_AREA_OF_EARTH,
            "Bob, ''",
    })
    public void PostForm_WithValidFields_Success(String gardenName, String gardenSize) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/create-garden")
                        .with(csrf())
                        .param("name", gardenName)
                        .param("location.streetAddress", "test")
                        .param("location.suburb", "test")
                        .param("location.city", "test")
                        .param("location.postcode", "0000")
                        .param("location.country", "test")
                        .param("size", gardenSize))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        verify(gardenService).addGarden(any(Garden.class));
    }

    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "'', 1.49",
            "'',''",
            "Flower_Garden,''",
            "myGarden, -1"
    })
    public void PostForm_WithInvalidFields_ErrorsShown(String gardenName, String gardenSize) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/create-garden")
                        .with(csrf())
                        .param("name", gardenName)
                        .param("location.streetAddress", "test")
                        .param("location.suburb", "test")
                        .param("location.city", "test")
                        .param("location.postcode", "0000")
                        .param("location.country", "test")
                        .param("size", gardenSize))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(gardenService, times(0)).addGarden(any(Garden.class));
    }

}

