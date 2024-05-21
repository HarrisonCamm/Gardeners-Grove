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
public interface FriendRequestRepository extends CrudRepository<FriendRequest, Long> {

    Optional<FriendRequest> findById(long id);
    List<FriendRequest> findAll();

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM FriendRequest f WHERE f.receiver = :receiver AND f.sender = :sender")
    boolean hasRequestSent(@Param("sender")  User sender,
                           @Param("receiver") User receiver);

    @Transactional
    @Modifying
    @Query("DELETE FROM FriendRequest f WHERE f.sender = :sender AND f.receiver = :receiver")
    int deleteBySender(@Param("sender") User sender, @Param("receiver") User receiver);
}
