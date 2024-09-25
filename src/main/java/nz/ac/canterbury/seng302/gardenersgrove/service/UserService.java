package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.FriendRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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

    /**
     * Updates and persists user information in the repository.
     *
     * This method updates a user's first name, last name, email, and date of birth based on the provided parameters.
     * It also sets the user's "noLastName" status, which indicates whether the user has a last name.
     *
     * @param user The user entity to be updated.
     * @param firstName The new first name to set for the user.
     * @param lastName The new last name to set for the user. If "noLastName" is true, this may be disregarded.
     * @param noLastName A boolean flag indicating if the user has a last name.
     * @param email The new email to set for the user.
     * @param dateOfBirth The new date of birth to set for the user, in a string format.
     * @return The updated and persisted user entity.
     */
    public User updateUser(User user, String firstName, String lastName, boolean noLastName, String email, String dateOfBirth) {
        user.setValues(firstName, lastName, noLastName, email, dateOfBirth);
        return userRepository.save(user);
    }

    /**
     * Updates User entity in the database.
     *
     * @param user the User entity to be updated
     * @return the User entity
     */
    public User updateUserFriends(User user) {
        return userRepository.save(user);
    }

    /**
     * Updates the password of a given user.
     *
     * @param user        The user whose password is to be updated.
     * @param newPassword The new password to set.
     * @return The updated User entity.
     */
    public User updateUserPassword(User user, String newPassword) {
        // Encode the new password
        String encodedPassword = passwordEncoder.encode(newPassword);
        // Set the user's password to the encoded one
        user.setPassword(encodedPassword);
        // Save the updated user in the repository
        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their email address.
     *
     * This method searches the repository for a user with the specified email.
     *
     * @param email The email address to search for.
     * @return The user entity if found, or null if no user exists with the given email.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Gets the currently logged-in user
     * @return The user
     */
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : this.getUserByEmail(authentication.getName());
    }

    /**
     *  Gets a user by their id
     * @param id The user id
     * @return The user
     */
    public User getUserByID(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Deletes a user from the repository
     * @param user The user to delete
     */
    public void deleteUser(User user) {
        userRepository.deleteUser(user);
    }

    /**
     * Searches for users based on a search query
     * @param searchQuery The search query
     * @return A list of users that exactly match the search query
     */
    public List<User> searchForUsers(String searchQuery, User currentUser) {
        String[] parts = searchQuery.split(" ");
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "";
        return userRepository.searchForUsers(searchQuery, firstName, lastName, currentUser.getUserId(), currentUser);
    }

    public List<FriendRequest> getSentFriendRequests(User user) {
        return userRepository.getSentFriendRequests(user.getUserId());
    }

    public List<FriendRequest> getPendingFriendRequests(User currentUser) {
        return userRepository.getPendingFriendRequests(currentUser.getUserId());
    }


    public boolean areUsersEqual(User user1, User user2) {
        return Objects.equals(user1.getUserId(), user2.getUserId()) &&
                user1.getFirstName().equals(user2.getFirstName()) &&
                user1.getLastName().equals(user2.getLastName()) &&
                user1.getEmail().equals(user2.getEmail()) &&
                user1.getPassword().equals(user2.getPassword()) &&
                user1.getDateOfBirth().equals(user2.getDateOfBirth());
    }

    /**
     * Adds blooms to the user's balance
     * @param user
     * @param blooms
     */
    public void addBlooms(User user, int blooms) {
        user.setBloomBalance(user.getBloomBalance() + blooms);
        userRepository.save(user);
    }

    /**
     * Charges the user a certain number of blooms
     * @param user
     * @param blooms
     */
    public void chargeBlooms(User user, int blooms) {
        user.setBloomBalance(user.getBloomBalance() - blooms);
        userRepository.save(user);
    }

    /**
     * Return true if the user can afford the item
     * @param user
     * @param item
     * @return
     */
    public boolean canAfford(User user, Item item) {
        return user.getBloomBalance() >= item.getPrice();
    }

    public void updateUserLastFreeSpinUsed(User user) {
        user.updateLastFreeSpinUsed();
        userRepository.save(user);
    }

    // Used to increment the number of delayed tags that were deemed inappropriate
    public void incrementInappropriateTagCount(Long userId) {
        userRepository.incrementInappropriateTagCount(userId);
    }

    //Find the top 10 users by bloom balance and returns them like a list
    public List<User> getTop10UsersByBloomBalance() {
        return userRepository.findTop10ByOrderByBloomBalanceDesc();
    }

    public Integer getUserRank(Long userId) {
        return userRepository.findUserRank(userId);
    }



}
