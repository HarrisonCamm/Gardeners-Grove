package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.FriendRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
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

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:searchQuery) OR " +
            "(LOWER(u.firstName) = LOWER(:firstName) AND LOWER(u.lastName) = LOWER(:lastName) AND u.noLastName = false) OR " +
            "(LOWER(u.firstName) = LOWER(:searchQuery) AND u.noLastName = true)")
    List<User> searchForUsers(@Param("searchQuery") String searchQuery,
                              @Param("firstName") String firstName,
                              @Param("lastName") String lastName);

    @Query("SELECT r from FriendRequest r WHERE r.sender.userId = :userId")
    List<FriendRequest> getSentFriendRequests(Long userId);
}
