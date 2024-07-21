package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.ResetPasswordFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.entity.VerificationToken;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.VerificationTokenRepository;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest(ResetPasswordFormController.class)
public class ResetPasswordFormControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VerificationTokenService verificationTokenService;

    @MockBean
    private AuthorityService authorityService;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private MailService mailService;

    @MockBean
    private ImageService imageService;

    @MockBean
    private static VerificationTokenRepository verificationTokenRepository;

    private static VerificationToken verificationToken;
    @BeforeEach
    public void setUp() {
        Mockito.reset(verificationTokenService, authorityService, userService, authenticationManager, mailService);
        User loggedInUser = new User(1L,"Test", "User", false, "user@gmail.com", "P@ssw0rd123", "");

        // Mock the services and repositories
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        verificationTokenService = Mockito.mock(VerificationTokenService.class);
        verificationTokenRepository = Mockito.mock(VerificationTokenRepository.class);
        mailService = Mockito.mock(MailService.class);
        userService = Mockito.mock(UserService.class);

        LocalDateTime expiryDate = LocalDateTime.now().plusDays(1);

        verificationToken = new VerificationToken(loggedInUser, "123456", expiryDate);

        // Mock the verification token service
        when(verificationTokenService.findAllTokens()).thenReturn(List.of(verificationToken));
        when(verificationTokenService.validateToken(any(String.class))).thenReturn(true);
        when(verificationTokenService.getUserByToken(any(String.class))).thenReturn(loggedInUser);
        when(verificationTokenService.createVerificationToken(any(User.class))).thenReturn(verificationToken);

        // Mock the verification token repository
        when(verificationTokenRepository.findByToken(any(String.class))).thenReturn(verificationToken);

        ResetPasswordFormController resetPasswordFormController = new ResetPasswordFormController( userService, authenticationManager, verificationTokenService, mailService);

        mockMvc = MockMvcBuilders.standaloneSetup(resetPasswordFormController).build();
    }

    @Test
    @WithMockUser
    public void whenPostResetPasswordWithValidToken_thenTokenIsDeleted() throws Exception {

        mockMvc.perform(post("/reset-password-form")
                        .with(csrf())
                        .param("newPassword", "Password1!")
                        .param("retypedPassword", "Password1!")
                        .param("token", verificationToken.getToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sign-in-form"));

        verify(verificationTokenService).deleteToken(verificationToken.getToken());
    }
}