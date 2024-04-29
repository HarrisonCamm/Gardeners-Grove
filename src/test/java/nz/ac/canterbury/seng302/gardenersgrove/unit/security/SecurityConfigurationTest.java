package nz.ac.canterbury.seng302.gardenersgrove.unit.security;

import nz.ac.canterbury.seng302.gardenersgrove.controller.*;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.security.CustomAuthenticationProvider;
import nz.ac.canterbury.seng302.gardenersgrove.security.SecurityConfiguration;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;

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
    private UserRepository userRepository;

    @MockBean
    private CustomAuthenticationProvider customAuthenticationProvider;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private PlantService plantService;

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
    @WithMockUser(username="user", roles={"USER"})
    void testAccessControl_UserRole_ExpectedHttpStatus(String url, int expectedStatus) throws Exception {
        User mockUser = new User("user@email.com", "User", "Name", "password");
        MockHttpServletRequestBuilder request = get(url);
        setupMockUserSession(request, mockUser);
        mockMvc.perform(request)
                .andExpect(status().is(expectedStatus));
    }

    /**
     * Helper function to set up a mock user session for the requests
     */
    private void setupMockUserSession(MockHttpServletRequestBuilder requestBuilder, User user) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        requestBuilder.sessionAttr(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext)
                .sessionAttr("user", user);
    }
}

