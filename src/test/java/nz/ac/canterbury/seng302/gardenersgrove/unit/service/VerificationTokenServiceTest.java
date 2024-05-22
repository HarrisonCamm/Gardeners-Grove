package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.entity.VerificationToken;
import nz.ac.canterbury.seng302.gardenersgrove.repository.VerificationTokenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.VerificationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VerificationTokenServiceTest {

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
    }

    @Test
    void createVerificationToken_ShouldCreateToken() {
        VerificationToken token = verificationTokenService.createVerificationToken(user);

        // Verify the token is created correctly
        assertNotNull(token);
        assertEquals(user, token.getUser());
        assertNotNull(token.getToken());
        assertTrue(token.getExpiryDate().isAfter(LocalDateTime.now()));

        verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Set up a valid token, with a user and expiration time
        String tokenString = "123456";
        VerificationToken token = new VerificationToken(user, tokenString, LocalDateTime.now().plusMinutes(10));
        when(verificationTokenRepository.findByToken(tokenString)).thenReturn(token);

        boolean isValid = verificationTokenService.validateToken(tokenString);
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Set up an invalid token scenario, no user or expiration time
        String tokenString = "123456";
        when(verificationTokenRepository.findByToken(tokenString)).thenReturn(null);

        boolean isValid = verificationTokenService.validateToken(tokenString);
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        String tokenString = "123456";
        VerificationToken token = new VerificationToken(user, tokenString, LocalDateTime.now().minusMinutes(1));
        when(verificationTokenRepository.findByToken(tokenString)).thenReturn(token);

        boolean isValid = verificationTokenService.validateToken(tokenString);
        assertFalse(isValid);
    }

    @Test
    void deleteToken_ShouldDeleteToken() {
        String tokenString = "123456";
        verificationTokenService.deleteToken(tokenString);

        verify(verificationTokenRepository, times(1)).deleteByToken(tokenString);
    }

    @Test
    void getUserByToken_ShouldReturnUser() {
        String tokenString = "123456";
        VerificationToken token = new VerificationToken(user, tokenString, LocalDateTime.now().plusMinutes(10));
        when(verificationTokenRepository.findByToken(tokenString)).thenReturn(token);

        User retrievedUser = verificationTokenService.getUserByToken(tokenString);
        assertEquals(user, retrievedUser);
    }

    @Test
    void cleanupExpiredTokens_ShouldDeleteExpiredTokens() {
        verificationTokenService.cleanupExpiredTokens();

        verify(verificationTokenRepository, times(1)).deleteAllExpiredSince(any(LocalDateTime.class));
    }
}


