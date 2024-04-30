package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.entity.VerificationToken;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class VerificationTokenService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    /**
     * Creates a verification token for a given user and sets an expiry time of 10 minutes.
     * @param user the user for whom the token is created
     * @return the created verification token
     */
    public VerificationToken createVerificationToken(User user) {
        Random random = new Random();

        // Generate a random 6-digit number
        String token = String.valueOf(random.nextInt(900000) + 100000);
//        String token = UUID.randomUUID().toString();
//        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(10);
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(30);
        VerificationToken verificationToken = new VerificationToken(user, token, expiryDate);
        return verificationTokenRepository.save(verificationToken);
    }

    /**
     * Validates the token and checks whether it is expired or not.
     * @param token the token to validate
     * @return true if the token is valid and not expired, false otherwise
     */
    public boolean validateToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        // Optionally activate the user account or perform any other necessary actions here
        return verificationToken != null && verificationToken.getExpiryDate().isAfter(LocalDateTime.now());
    }

    /**
     * Deletes a specific token.
     * @param token the token to delete
     */
    public void deleteToken(String token) {
        verificationTokenRepository.deleteByToken(token);
    }

    /**
     * Retrieves the user associated with a given token.
     * @param token the token to search for
     * @return the user associated with the token, or null if the token is not found
     */
    public User getUserByToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        return verificationToken != null ? verificationToken.getUser() : null;
    }

    public VerificationToken getTokenByUser(User user) {
        return verificationTokenRepository.findByUser(user);
    }

    /**
     * Scheduled task to clean up expired tokens.
     * Runs every minute and removes tokens that have passed their expiry date.
     */
    public void cleanupExpiredTokens() {
        verificationTokenRepository.deleteAllExpiredSince(LocalDateTime.now());

    }

    public List<VerificationToken> findAllTokens() {
        return verificationTokenRepository.findAll();
    }
}
