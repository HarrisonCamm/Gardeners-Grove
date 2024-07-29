package nz.ac.canterbury.seng302.gardenersgrove.service;


import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.entity.UserRelationship;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRelationshipService {
    private final UserRelationshipRepository userRelationshipRepository;

    @Autowired
    public UserRelationshipService(UserRelationshipRepository userRelationshipRepository) {
        this.userRelationshipRepository = userRelationshipRepository;
    }
    public void save(UserRelationship userRelationship) {
        userRelationshipRepository.save(userRelationship);
    }

    public List<UserRelationship> getAllRelationships() {
        return userRelationshipRepository.findAll();
    }
    public List<UserRelationship> getUserRelationships(User sender) {
        return userRelationshipRepository.findUserRelationshipBySender(sender);
    }

    public UserRelationship getRelationship(User receiver, User sender) {
        return userRelationshipRepository.findUserRelationshipByReceiverAndSender(receiver, sender).orElse(null);
    }
    public void remove(UserRelationship userRelationship) {
        userRelationshipRepository.delete(userRelationship);
    }

    public void removeAll() {
        userRelationshipRepository.deleteAll();
    }
}
