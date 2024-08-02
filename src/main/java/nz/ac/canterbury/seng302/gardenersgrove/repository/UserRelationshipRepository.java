package nz.ac.canterbury.seng302.gardenersgrove.repository;


import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.entity.UserRelationship;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRelationshipRepository extends CrudRepository<UserRelationship, Long> {
    List<UserRelationship> findAll();
    Optional<UserRelationship> findUserRelationshipByReceiverAndSender(User receiver, User sender);
    List<UserRelationship> findUserRelationshipBySender(User sender);


}
