package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Adds User entity into the database.
     *
     * @param user the User entity to be added
     * @return the User entity
     */
    public User addUser(User user) {
        // Encodes the users password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        // Updates users password to the encrypted one
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    /**
     * Validates the user's email and password.
     *
     * Find a user by their email address.
     * Check if the password entered matches users password
     * @param email    The email address of the user to validate.
     * @param password The password of the user to validate.
     * @return An Optional containing the found user if the validation is successful,
     *         or an empty Optional if the user is not found or if the password does not match.
     */
    public Optional<User> validateUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent() && (passwordEncoder.matches(password, user.get().getPassword()) || user.get().getPassword().equals(password))) {
            return user;
        }
        return Optional.empty();
    }

    /**
     * Checks if an email address already exists in the user repository.
     *
     * @param email The email address to check.
     * @return true if the email exists, false otherwise.
     */
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User updateUser(User user, String firstName, String lastName, boolean noLastName, String email, String dateOfBirth) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setNoLastName(noLastName);
        user.setEmail(email);
        user.setDateOfBirth(dateOfBirth);
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
