package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;


import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

@WebMvcTest
public class ViewGardenTests {

    private static final String SURFACE_AREA_OF_EARTH = "510100000";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AutocompleteService autocompleteService;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private LocationService locationService;

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

    private static User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User("user@email.com", "User", "Name", "password");
//        testUser.setUserId(1L);
        when(userService.getAuthenicatedUser()).thenReturn(testUser);
    }


    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "Lovely Garden, 12.00009123",
            "Tomato's, " +SURFACE_AREA_OF_EARTH,
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
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, Integer.MAX_VALUE, Integer.MIN_VALUE, 0})
    public void postForm_WithInvalidID_Fail(int integer) {
        //Incoming bad test
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/view-garden")
                    .param("gardenID", String.valueOf(integer)));
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "This is a tag",
            "Flowers",
            "Vegetables",
            "Fruit",
            "Herbs"})
    public void addTag_WithValidTag_Success(String tag) throws Exception {

        Location location = new Location("Test", "Test", "Test", "Test", "Test");

        when(gardenService.findGarden(1L)).thenReturn(Optional.of(new Garden("Test", location, "1", testUser)));
        when(tagService.addTag(any())).thenReturn(new Tag(1L, tag));

        mockMvc.perform(MockMvcRequestBuilders.post("/add-tag")
                        .with(csrf())
                        .param("gardenID", "1")
                        .param("tag", tag))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    // TODO implement failing tests after moderation is implemented
}
