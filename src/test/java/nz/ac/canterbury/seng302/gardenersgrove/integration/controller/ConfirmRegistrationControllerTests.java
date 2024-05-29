package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.ConfirmRegistrationController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Authority;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ConfirmRegistrationController.class)
public class ConfirmRegistrationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VerificationTokenService verificationTokenService;

    @MockBean
    private AuthorityService authorityService;

    @MockBean
    private MailService mailService;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private ImageService imageService;

    @BeforeEach
    public void setUp() {
        Mockito.reset(verificationTokenService, authorityService, userService, authenticationManager);
    }

    @Test
    @WithMockUser
    public void whenPostConfirmRegistrationWithValidCode_thenRedirectsToSignInForm() throws Exception {
        User mockUser = mock(User.class);
        when(verificationTokenService.validateToken("valid-code")).thenReturn(true);
        when(verificationTokenService.getUserByToken("valid-code")).thenReturn(mockUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        mockMvc.perform(post("/confirm-registration")
                        .with(csrf())
                        .param("registrationCode", "valid-code"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sign-in-form"));

        verify(verificationTokenService).deleteToken("valid-code");
        verify(authorityService).deleteByUser(mockUser);
        verify(mockUser).grantAuthority("ROLE_USER");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @WithMockUser
    public void whenGetConfirmRegistration_thenReturnsFormView() throws Exception {
        when(verificationTokenService.findAllTokens()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/confirm-registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("confirmRegistrationTemplate"));

        verify(verificationTokenService, atLeastOnce()).findAllTokens();
    }

    @Test
    @WithMockUser
    public void whenPostConfirmRegistrationWithInvalidCode_thenReturnsFormViewWithError() throws Exception {
        when(verificationTokenService.validateToken("invalid-code")).thenReturn(false);

        mockMvc.perform(post("/confirm-registration")
                        .with(csrf())
                        .param("registrationCode", "invalid-code"))
                .andExpect(status().isOk())
                .andExpect(view().name("confirmRegistrationTemplate"))
                .andExpect(model().attributeExists("signupCodeError"));

        verify(verificationTokenService, never()).deleteToken(anyString());
        verify(authorityService, never()).deleteByUser(any(User.class));
    }

    @Test
    public void testClearExpiredUsers() {
        User mockUser = mock(User.class);
        Authority mockAuthority = mock(Authority.class);

        when(mockAuthority.getUser()).thenReturn(mockUser);
        when(authorityService.findByRole("ROLE_UNVERIFIED")).thenReturn(List.of(mockAuthority));
        when(verificationTokenService.getTokenByUser(mockUser)).thenReturn(null);

        ConfirmRegistrationController controller = new ConfirmRegistrationController(userService, authenticationManager, verificationTokenService, authorityService);
        controller.clearExpiredUsers();

        verify(authorityService).deleteByUser(mockUser);
        verify(userService).deleteUser(mockUser);
    }
}
