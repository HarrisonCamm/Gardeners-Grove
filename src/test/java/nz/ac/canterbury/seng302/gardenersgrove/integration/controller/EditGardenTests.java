package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.AutocompleteController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.CreateGardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest
public class EditGardenTests {

    private static final String SURFACE_AREA_OF_EARTH = "510100000";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AutocompleteController autocompleteController;

    @MockBean
    private LocationService locationService;

    @MockBean
    private PlantService PlantService;

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

    private User mockUser;

    @BeforeEach
    public void setup() {
        mockUser = new User("user@email.com", "User", "Name", "password");
//        mockUser.setUserId(1L);
        Mockito.when(userService.getAuthenticatedUser()).thenReturn(mockUser);
    }

    @Test
    public void RequestPage_NoFields_Failure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/edit-garden"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        verify(gardenService, times(0)).findGarden(any(Long.class));
    }

    @Test
    @WithMockUser
    public void RequestPage_InvalidID_Failure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/edit-garden")
                    .param("gardenID", "0"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        verify(gardenService).findGarden(0L);
    }

    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "325, My Garden, 12.00009123",
            "211, Tomato's, " + SURFACE_AREA_OF_EARTH,
            "1955, Bob, ''",
    })
    public void PutForm_WithValidFields_Success(Long gardenID, String gardenName, String gardenSize) throws Exception {
        Location testLocation = new Location("123 test street", "test", "test", "0000", "test");
        when(gardenService.findGarden(gardenID)).thenReturn(Optional.of(new Garden(gardenName, testLocation, gardenSize, mockUser)));
        mockMvc.perform(MockMvcRequestBuilders.put("/edit-garden")
                        .with(csrf())
                    .param("gardenID", gardenID.toString())
                    .param("name", gardenName)
                    .param("location.streetAddress", testLocation.getStreetAddress())
                    .param("location.suburb", testLocation.getSuburb())
                    .param("location.city", testLocation.getCity())
                    .param("location.postcode", testLocation.getPostcode())
                    .param("location.country", testLocation.getCountry())
                    .param("size", gardenSize))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/view-garden?gardenID=*"));
        verify(gardenService).findGarden(gardenID);
    }

    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "1, '', 1.49,  My Garden, 12.00009123",
            "1, '', '',  Tomato's, " + Long.MAX_VALUE,
            "1, '', '',  Bob, ''",
            "1, myGarden, -1,  The Mall, 143,5553"
    })
    public void PutForm_WithInvalidFields_ErrorsShown(
            Long gardenID, String newName, String newSize,
                           String oldName, String oldSize) throws Exception {

        Location newLocation = new Location("123 test street", "test", "test", "0000", "test");
        Location oldLocation = newLocation;
        Garden garden = new Garden(oldName, oldLocation, oldSize, mockUser);
        when(gardenService.findGarden(gardenID)).thenReturn(Optional.of(garden));
        mockMvc.perform(MockMvcRequestBuilders.put("/edit-garden")
                        .with(csrf())
                        .param("gardenID", gardenID.toString())
                        .param("name", newName)
                        .param("location.streetAddress", newLocation.getStreetAddress())
                        .param("location.suburb", newLocation.getSuburb())
                        .param("location.city", newLocation.getCity())
                        .param("location.postcode", newLocation.getPostcode())
                        .param("location.country", newLocation.getCountry())
                        .param("size", newSize))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(gardenService).findGarden(gardenID);
        verify(gardenService, times(0)).addGarden(any(Garden.class));
    }

    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "52334, My Garden, 12.00009123",
            "76451, Tomato's, " + Long.MAX_VALUE,
            "34534555, Bob, ''",
            "8, The Mall, 143,5553"
    })
    public void OnForm_CancelEdit_RedirectToViewGarden(
            Long gardenID, String name, String size) throws Exception {
        Location location = new Location("123 test street", "test", "test", "0000", "test");
        Garden garden = new Garden(name, location, size, mockUser);
        when(gardenService.findGarden(gardenID)).thenReturn(Optional.of(garden));
        RedirectService.addEndpoint("/view-garden?gardenID=" + gardenID);

        mockMvc.perform(MockMvcRequestBuilders.get("/Cancel"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/view-garden?gardenID=" + gardenID));
        verify(gardenService, never()).addGarden(any(Garden.class));

        Optional<Garden> foundOpt = gardenService.findGarden(gardenID);
        if (foundOpt.isPresent()) {
            Garden found = foundOpt.get();
            assertEquals(garden.getName(), found.getName());
            assertEquals(garden.getLocation(), found.getLocation());
            assertEquals(garden.getSize(), found.getSize());
        } else {
            fail();
        }
    }
}

