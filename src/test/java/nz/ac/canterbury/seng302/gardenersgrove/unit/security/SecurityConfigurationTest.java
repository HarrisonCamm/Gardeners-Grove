package nz.ac.canterbury.seng302.gardenersgrove.unit.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.controller.*;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.security.CustomAuthenticationProvider;
import nz.ac.canterbury.seng302.gardenersgrove.security.SecurityConfiguration;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
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

    @BeforeEach
    void setUp() {
//        // Create new user
//        User mockUser = new User("user@email.com", "User", "Name", "password");
//
//        // Grant user role
//        mockUser.grantAuthority("ROLE_USER");
//
//        // Register user
//        userService.addUser(mockUser);
//
//        // Auto-login security stuff
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("user@email.com", "password");
//        Authentication authentication = authenticationManager.authenticate(token);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Define the behavior of request.getSession() to return the mocked HttpSession
//        when(request.getSession()).thenReturn(session);
//
//        // Set the authenticated user in the session
//        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
//
//        // Set the authenticated user in the session
//        request.getSession().setAttribute("user", mockUser);

        // Jakes help code
//        mockUser.grantAuthority("ROLE_USER");
//
//        Authentication authentication = Mockito.mock(Authentication.class);
//        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
//
//        when(authentication.getPrincipal()).thenReturn(mockUser);
//        when(authentication.isAuthenticated()).thenReturn(true);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//
//        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * Parameterized test to verify the URL access control for a user with the role "USER".
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
            "/view-garden,200",
            "/edit-garden,200",
            "/create-plant,200",
            "/edit-plant,200",
            "/admin,403"
    })
//    @WithMockUser(value="user@email.com", authorities = {"ROLE_USER"})
    void testAccessControl_UserRole_ExpectedHttpStatus(String url, int expectedStatus) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().is(expectedStatus));
    }
}

