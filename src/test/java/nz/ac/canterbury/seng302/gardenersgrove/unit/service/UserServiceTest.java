package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.FriendRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    public void updateUserPassword_ShouldCorrectlyUpdatePassword() {
        User testUser = new User("Aroha", "Greenwood", false, "aroha@gmail.com", "1!Password", "2000-11-03");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User updatedUser = userService.updateUserPassword(testUser, "NewPassword");

        Mockito.verify(userRepository).save(testUser);
        assertTrue(passwordEncoder.matches("NewPassword", updatedUser.getPassword()));
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

    @Test
    public void getAuthenicatedUser_ShouldReturnAuthenticatedUser() {
        User testUser = new User("Aroha", "Greenwood", false, "aroha@gmail.com", "1!Password", "2000-11-03");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("aroha@gmail.com");
        when(userRepository.findByEmail("aroha@gmail.com")).thenReturn(Optional.of(testUser));
        SecurityContextHolder.setContext(securityContext);

        User result = userService.getAuthenicatedUser();
        assertEquals(testUser, result);
    }

    @Test
    public void getUserByID_ShouldReturnUserForExistingID() {
        User testUser = new User("Aroha", "Greenwood", false, "aroha@gmail.com", "1!Password", "2000-11-03");
        testUser.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserByID(1L);
        assertEquals(testUser, result);

        result = userService.getUserByID(2L);
        assertNull(result);
    }

    @Test
    public void deleteUser_ShouldCallRepositoryDelete() {
        User testUser = new User("Aroha", "Greenwood", false, "aroha@gmail.com", "1!Password", "2000-11-03");
        doNothing().when(userRepository).deleteUser(testUser);

        userService.deleteUser(testUser);
        verify(userRepository, times(1)).deleteUser(testUser);
    }

    @Test
    public void searchForUsers_ShouldReturnMatchingUsers() {
        // Setup
        User currentUser = new User("current@example.com", "Current", "User", "password");
        currentUser.setUserId(1L); // Assign an ID to simulate a persisted user.
        User testUser = new User("Aroha", "Greenwood", false, "aroha@gmail.com", "1!Password", "2000-11-03");
        testUser.setUserId(2L); // Assign an ID to another user for distinction.

        // Mock expected behavior: Return testUser when search criteria match.
        String searchQuery = "Aroha Greenwood";
        String[] parts = searchQuery.split(" ");
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "";

        List<User> expectedUsers = List.of(testUser);
        when(userRepository.searchForUsers(
                eq(searchQuery.toLowerCase()), // The search query should be converted to lower case.
                eq(firstName.toLowerCase()),   // The first name should be converted to lower case.
                eq(lastName.toLowerCase()),    // The last name should be converted to lower case.
                eq(currentUser.getUserId()),   // The current user's ID should be used to exclude them from results.
                eq(currentUser)               // The current user object is needed for the query.
        )).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.searchForUsers(searchQuery, currentUser);

        // Assert
        assertEquals(1, result.size(), "Expected to find exactly one user.");
        assertEquals(testUser, result.get(0), "The returned user should match the expected test user.");
    }

    @Test
    public void getSentFriendRequests_ShouldReturnFriendRequests() {
        User testUser = new User("Aroha", "Greenwood", false, "aroha@gmail.com", "1!Password", "2000-11-03");
        List<FriendRequest> friendRequests = new ArrayList<>();
        when(userRepository.getSentFriendRequests(testUser.getUserId())).thenReturn(friendRequests);

        List<FriendRequest> result = userService.getSentFriendRequests(testUser);
        assertEquals(friendRequests, result);
    }

    @Test
    public void getPendingFriendRequests_ShouldReturnPendingFriendRequests() {
        User testUser = new User("Aroha", "Greenwood", false, "aroha@gmail.com", "1!Password", "2000-11-03");
        List<FriendRequest> friendRequests = new ArrayList<>();
        when(userRepository.getPendingFriendRequests(testUser.getUserId())).thenReturn(friendRequests);

        List<FriendRequest> result = userService.getPendingFriendRequests(testUser);
        assertEquals(friendRequests, result);
    }
}
