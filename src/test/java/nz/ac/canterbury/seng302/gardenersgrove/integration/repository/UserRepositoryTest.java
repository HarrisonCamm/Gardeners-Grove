package nz.ac.canterbury.seng302.gardenersgrove.integration.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import org.junit.jupiter.api.Assertions;
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
    private UserService userService;

    @MockBean
    private ImageService imageService;

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

        List<User> users = userRepository.searchForUsers(searchQuery, firstName, lastName);

        Assertions.assertNotEquals(0, users.size());
        Assertions.assertEquals(savedUser.getEmail(), users.get(0).getEmail());
    }
}