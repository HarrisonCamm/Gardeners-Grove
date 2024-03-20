package nz.ac.canterbury.seng302.gardenersgrove.security;

import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.security.CustomAuthenticationProvider;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNull;

public class CustomAuthenticationProviderTest {

    @Mock
    private UserService userService;

    private CustomAuthenticationProvider customAuthenticationProvider;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        customAuthenticationProvider = new CustomAuthenticationProvider(userService);
    }

    @Test
    public void testAuthenticate_InvalidCredentials_ThrowsBadCredentialsException() {
        // Arrange
        String email = "test@test.com";
        String password = "wrongPassword";
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);

        when(userService.validateUser(email, password)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(BadCredentialsException.class, () -> customAuthenticationProvider.authenticate(authentication));
    }

    @Test
    public void testAuthenticate_EmptyPassword_ThrowsBadCredentialsException() {
        // Arrange
        String email = "test@test.com";
        String password = "";
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);

        // Act and Assert
        assertThrows(BadCredentialsException.class, () -> customAuthenticationProvider.authenticate(authentication));
    }

    @Test
    public void testAuthenticate_EmptyEmail_ThrowsBadCredentialsException() {
        // Arrange
        String email = "";
        String password = "password";
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);

        // Act and Assert
        assertThrows(BadCredentialsException.class, () -> customAuthenticationProvider.authenticate(authentication));
    }
}