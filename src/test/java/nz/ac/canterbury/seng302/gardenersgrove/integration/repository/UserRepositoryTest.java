package nz.ac.canterbury.seng302.gardenersgrove.integration.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(UserService.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private static UserService userService;

    @MockBean
    private ImageService imageService;

    private static User loggedUser;

    @BeforeAll
    public static void setUp() {
        loggedUser = new User("logged@email.com", "logged", "person", "password");
    }

    @Test
    public void findById_ShouldReturnUser_WhenUserExists() {
        // Create a new user
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        // Save the user to the repository
        User savedUser = userRepository.save(user);

        // Attempt to retrieve the user
        Optional<User> retrievedUser = userRepository.findById(savedUser.getUserId());

        // Check that the user was retrieved successfully
        Assertions.assertTrue(retrievedUser.isPresent());
        Assertions.assertEquals(savedUser.getUserId(), retrievedUser.get().getUserId());
        Assertions.assertEquals(savedUser.getEmail(), retrievedUser.get().getEmail());
        Assertions.assertEquals(savedUser.getFirstName(), retrievedUser.get().getFirstName());
        Assertions.assertEquals(savedUser.getLastName(), retrievedUser.get().getLastName());
    }

    @Test
    public void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // Create a new user
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        // Save the user to the repository
        User savedUser = userRepository.save(user);

        // Attempt to retrieve the user
        Optional<User> retrievedUser = userRepository.findByEmail(savedUser.getEmail());

        // Check that the user was retrieved successfully
        Assertions.assertTrue(retrievedUser.isPresent());
        Assertions.assertEquals(savedUser.getEmail(), retrievedUser.get().getEmail());
        Assertions.assertEquals(savedUser.getFirstName(), retrievedUser.get().getFirstName());
        Assertions.assertEquals(savedUser.getLastName(), retrievedUser.get().getLastName());
    }


    @ParameterizedTest
    @CsvSource({
            "Test User, Test, 'User'",
            "test user, test, 'user'",
            "test@example.com, '', ''"
    })
    public void searchForUsers_ShouldReturnUser_WhenUserExists(String searchQuery, String firstName, String lastName) {
        // Create a new user
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        User savedUser = userRepository.save(user);
        userRepository.save(loggedUser);

        List<User> users = userRepository.searchForUsers(searchQuery, firstName, lastName, loggedUser.getUserId());

        Assertions.assertNotEquals(0, users.size());
        Assertions.assertEquals(savedUser.getEmail(), users.get(0).getEmail());
    }

    @Test
    public void searchForUsers_ShouldReturnTwoUsers_WhenSameName() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setFirstName("Test");
        user1.setLastName("User");

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setFirstName("Test");
        user2.setLastName("User");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(loggedUser);

        List<User> users = userRepository.searchForUsers("Test User", "Test", "User", loggedUser.getUserId());

        Assertions.assertEquals(2, users.size());
    }
}