package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.FriendRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Transaction;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{

    @Override
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u = :user")
    void deleteUser(User user);

    @Query("SELECT u FROM User u WHERE u.userId != :userId AND NOT (:currentUser MEMBER OF u.friends) AND (LOWER(u.email) = LOWER(:searchQuery) OR " +
            "(LOWER(u.firstName) = LOWER(:firstName) AND LOWER(u.lastName) = LOWER(:lastName) AND u.noLastName = false) OR " +
            "(LOWER(u.firstName) = LOWER(:searchQuery) AND u.noLastName = true))")
    List<User> searchForUsers(@Param("searchQuery") String searchQuery,
                              @Param("firstName") String firstName,
                              @Param("lastName") String lastName,
                              @Param("userId") Long userId,
                              @Param("currentUser") User currentUser);

    @Query("SELECT r from FriendRequest r WHERE r.sender.userId = :userId")
    List<FriendRequest> getSentFriendRequests(Long userId);


    @Query("SELECT r from FriendRequest r WHERE r.receiver.userId = :userId and r.status = 'Pending'")
    List<FriendRequest> getPendingFriendRequests(Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.inappropriateTagCount = u.inappropriateTagCount + 1 WHERE u.userId = :userId")
    void incrementInappropriateTagCount(@Param("userId") Long userId);


    /**
     * Finds the top 10 users by bloom balance, ordered from highest to lowest.
     *
     * @return a list of the top 10 users sorted by their bloom balance in descending order.
     */
    @Query("SELECT u FROM User u ORDER BY u.bloomBalance DESC LIMIT 10")
    List<User> findTop10ByOrderByBloomBalanceDesc();

    /**
     * Finds the rank of a user based on their bloom balance.
     * The rank is determined by counting how many users have a higher bloom balance.
     *
     * @param userId the ID of the user whose rank is to be determined.
     * @return the rank of the user, where the rank is 1 for the highest bloom balance.
     */
    @Query("SELECT COUNT(u) + 1 FROM User u WHERE u.bloomBalance > (SELECT bloomBalance FROM User WHERE userId = :userId)")
    Integer findUserRank(@Param("userId") Long userId);


}
