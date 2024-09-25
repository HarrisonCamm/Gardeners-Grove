package nz.ac.canterbury.seng302.gardenersgrove.unit.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.controller.*;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ShopRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.security.CustomAuthenticationProvider;
import nz.ac.canterbury.seng302.gardenersgrove.security.SecurityConfiguration;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {
        SecurityConfiguration.class,
        HomeController.class,
        RegisterFormController.class,
        SignInController.class,
        MainController.class,
        UserProfileController.class,
        EditProfileController.class,
        CreateGardenController.class,
        ViewGardensController.class,
        ViewGardenController.class,
        EditGardenController.class,
        CreatePlantController.class,
        EditPlantController.class,
})
public class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private HttpServletRequest request;

    @MockBean
    private HttpSession session;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CustomAuthenticationProvider customAuthenticationProvider;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private PlantService plantService;

    @MockBean
    private VerificationTokenService verificationTokenService;

    @MockBean
    private AuthorityService authorityService;

    @MockBean
    private MailService mailService;

    @MockBean
    private ImageService imageService;

    @MockBean
    private ShopRepository shopRepository;

    @MockBean
    private ShopService shopService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private TagService tagService;

    @MockBean
    private WeatherService weatherService;

    @MockBean
    private ModerationService moderationService;

    @MockBean
    private AlertService alertService;

    @MockBean
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        // Create new user
        User mockUser = new User("user@email.com", "User", "Name", "password");
        Mockito.when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        // Create new garden
        Garden mockGarden = Mockito.mock(Garden.class);
        Mockito.when(gardenService.findGarden(1L)).thenReturn(Optional.of(mockGarden));
        Mockito.when(mockGarden.getOwner()).thenReturn(mockUser);
        Mockito.when(mockGarden.getLocation()).thenReturn(Mockito.mock(Location.class));

        // Create new plant
        Plant mockPlant = Mockito.mock(Plant.class);
        Mockito.when(plantService.findPlant(1L)).thenReturn(Optional.of(mockPlant));
        Mockito.when(mockPlant.getGarden()).thenReturn(mockGarden);
    }

    /**
     * Parameterized test to verify the URL access control for a user with the role "USER"
     * The 302-status code is expected because spring security redirects you to sign in page
     */
    @ParameterizedTest
    @CsvSource({
            "/home,200",
            "/sign-in-form,200",
            "/register-form,200",
            "/main,200",
            "/view-user-profile,200",
            "/edit-user-profile,200",
            "/create-garden,200",
            "/view-gardens,200",
            "/view-garden,400",
            "/edit-garden,400",
            "/create-plant,400",
            "/edit-plant,400",
//            "/admin,403",
            "/view-garden?gardenID=1,200",
            "/edit-garden?gardenID=1,200",
            "/create-plant?gardenID=1,200",
            "/edit-plant?plantID=1,200",
    })
    @WithMockUser(value="user@email.com")
    void testAccessControl_UserRole_ExpectedHttpStatus(String url, int expectedStatus) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().is(expectedStatus));
    }

    /**
     * Parameterized test to verify the URL access control for a user with no role
     */
    @ParameterizedTest
    @CsvSource({
            "/home,200",
            "/sign-in-form,200",
            "/register-form,200",
            "/main,302",
            "/view-user-profile,302",
            "/edit-user-profile,302",
            "/create-garden,302",
            "/view-gardens,302",
            "/view-garden,302",
            "/edit-garden,302",
            "/create-plant,302",
            "/edit-plant,302",
            "/admin,302",
            "/view-garden?gardenID=1,302",
            "/edit-garden?gardenID=1,302",
            "/create-plant?gardenID=1,302",
            "/edit-plant?plantID=1,302",
    })
    void testAccessControl_UnauthenticatedUser_ExpectedHttpStatus(String url, int expectedStatus) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().is(expectedStatus));
    }
}

