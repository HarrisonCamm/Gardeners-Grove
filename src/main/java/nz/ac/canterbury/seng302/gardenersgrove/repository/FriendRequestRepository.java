package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.FriendRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends CrudRepository<FriendRequest, Long> {

    Optional<FriendRequest> findById(long id);
    List<FriendRequest> findAll();

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM FriendRequest f WHERE f.receiver = :receiver AND f.sender = :sender")
    boolean hasRequestSent(@Param("sender")  User sender,
                           @Param("receiver") User receiver);
}
