package nz.ac.canterbury.seng302.gardenersgrove.unit.security;

import nz.ac.canterbury.seng302.gardenersgrove.security.CustomAuthenticationProvider;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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
        String email = "test@test.com";
        String password = "wrongPassword";
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);

        when(userService.validateUser(email, password)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> customAuthenticationProvider.authenticate(authentication));
    }

    @Test
    public void testAuthenticate_EmptyPassword_ThrowsBadCredentialsException() {
        String email = "test@test.com";
        String password = "";
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);

        assertThrows(BadCredentialsException.class, () -> customAuthenticationProvider.authenticate(authentication));
    }

    @Test
    public void testAuthenticate_EmptyEmail_ThrowsBadCredentialsException() {
        String email = "";
        String password = "password";
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);

        assertThrows(BadCredentialsException.class, () -> customAuthenticationProvider.authenticate(authentication));
    }
}