package nz.ac.canterbury.seng302.gardenersgrove.integration.repository;

import jakarta.persistence.EntityManager;
import nz.ac.canterbury.seng302.gardenersgrove.entity.FriendRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendRequestRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendRequestService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.Optional;
@DataJpaTest
@Import(FriendRequestService.class)
public class FriendRequestRepositoryTest {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @MockBean
    private FriendRequestService friendRequestService;

    @MockBean
    private UserService userService;

    @MockBean
    private ImageService imageService;

    private static User test1;
    private static User test2;

    @BeforeAll
    public static void setUp() {
        test1 = new User("test1@email.com", "test1", "user1", "password");
        test2 = new User("test2@email.com", "test2", "user2", "password");
    }

    @Test
    public void findById_ShouldReturnFriendRequest_WhenFriendRequestExists() {
        FriendRequest friendRequest = new FriendRequest(test1, test2);
        FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequest);
        Optional<FriendRequest> retrievedFriendRequest = friendRequestRepository.findById(savedFriendRequest.getId());
        Assertions.assertTrue(retrievedFriendRequest.isPresent());
    }

    @Test
    void deleteBySender_ShouldDeleteFriendRequest_WhenFriendRequestExists() {
        // Save the users in the database
        test1 = userRepository.save(test1);
        test2 = userRepository.save(test2);

        // Create and save the friend request
        FriendRequest friendRequest = new FriendRequest(test1, test2);
        friendRequestRepository.save(friendRequest);

        // Retrieve the managed entities
        test1 = userRepository.findById(test1.getUserId()).get();
        test2 = userRepository.findById(test2.getUserId()).get();

        // Delete the friend request
        friendRequestRepository.deleteBySender(test1, test2);

        // Flush and clear the persistence context
        entityManager.flush();
        entityManager.clear();

        // Check that the friend request was deleted
        Optional<FriendRequest> retrievedFriendRequest = friendRequestRepository.findById(friendRequest.getId());
        Assertions.assertFalse(retrievedFriendRequest.isPresent());
    }

    @Test
    void alterStatus_ShouldUpdateStatus_WhenFriendRequestExists() {
        // Save the users in the database
        test1 = userRepository.save(test1);
        test2 = userRepository.save(test2);

        // Create and save the friend request
        FriendRequest friendRequest = new FriendRequest(test1, test2);
        friendRequestRepository.save(friendRequest);

        // Retrieve the managed entities
        test1 = userRepository.findById(test1.getUserId()).get();
        test2 = userRepository.findById(test2.getUserId()).get();

        // Update the status of the friend request
        friendRequestRepository.alterStatus(test2, test1, "ACCEPTED");

        // Flush and clear the persistence context
        entityManager.flush();
        entityManager.clear();

        // Check that the status was updated
        Optional<FriendRequest> retrievedFriendRequest = friendRequestRepository.findById(friendRequest.getId());
        Assertions.assertTrue(retrievedFriendRequest.isPresent());
        Assertions.assertEquals("ACCEPTED", retrievedFriendRequest.get().getStatus());
    }
}
