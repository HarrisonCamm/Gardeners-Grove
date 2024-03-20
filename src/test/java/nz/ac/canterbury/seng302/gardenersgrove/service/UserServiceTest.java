package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    public void addUser_ShouldEncryptPasswordCorrectly() {
        User testUser = new User("Aroha", "Greenwood", false, "aroha@gmail.com", "1!Password", "2000-11-03");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User result = userService.addUser(testUser);

        Mockito.verify(userRepository).save(testUser);
        assertTrue(passwordEncoder.matches("1!Password", result.getPassword()));
    }

    @Test
    public void validateUser_ShouldSucceedWithCorrectCredentials() {
        User testUser = new User("Aroha", "Greenwood", false, "aroha@gmail.com", "1!Password", "2000-11-03");
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        when(userRepository.findByEmail("aroha@gmail.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.validateUser("aroha@gmail.com", "1!Password");
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());

        result = userService.validateUser("aroha@gmail.com", "wrongPassword");
        assertFalse(result.isPresent());
    }

    @Test
    public void validateUser_ShouldFailWithIncorrectCredentials() {
        User testUser = new User("Aroha", "Greenwood", false, "aroha@gmail.com", "1!Password", "2000-11-03");
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        when(userRepository.findByEmail("aroha@gmail.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.validateUser("aroha@gmail.com", "wrongPassword");
        assertFalse(result.isPresent());
    }

    @Test
    public void emailExists_ShouldReturnTrueForExistingEmail() {
        User testUser = new User("Aroha", "Greenwood", false, "aroha@gmail.com", "1!Password", "2000-11-03");
        when(userRepository.findByEmail("aroha@gmail.com")).thenReturn(Optional.of(testUser));

        boolean result = userService.emailExists("aroha@gmail.com");
        assertTrue(result);

        result = userService.emailExists("wrongEmail@gmail.com");
        assertFalse(result);
    }

    @Test
    public void updateUser_ShouldCorrectlyUpdateUserDetails() {
        User testUser = new User("Aroha", "Greenwood", false, "aroha@gmail.com", "1!Password", "2000-11-03");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User updatedUser = userService.updateUser(testUser, "NewFirstName", "NewLastName", true, "newEmail@gmail.com", "2001-01-01");

        Mockito.verify(userRepository).save(testUser);
        assertEquals("NewFirstName", updatedUser.getFirstName());
        assertEquals("NewLastName", updatedUser.getLastName());
        assertTrue(updatedUser.getNoLastName());
        assertEquals("newEmail@gmail.com", updatedUser.getEmail());
        assertEquals("2001-01-01", updatedUser.getDateOfBirth());
    }

    @Test
    public void getUserByEmail_ShouldReturnUserForExistingEmail() {
        User testUser = new User("Aroha", "Greenwood", false, "aroha@gmail.com", "1!Password", "2000-11-03");
        when(userRepository.findByEmail("aroha@gmail.com")).thenReturn(Optional.of(testUser));

        User result = userService.getUserByEmail("aroha@gmail.com");
        assertEquals(testUser, result);

        result = userService.getUserByEmail("wrongEmail@gmail.com");
        assertNull(result);
    }

    @Test
    public void getUserByEmail_ShouldReturnNullForNonexistentEmail() {
        when(userRepository.findByEmail("nonexistent@gmail.com")).thenReturn(Optional.empty());

        User result = userService.getUserByEmail("nonexistent@gmail.com");
        assertNull(result);
    }
}