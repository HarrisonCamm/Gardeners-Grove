package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.FriendRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;

    @Autowired
    public FriendRequestService(FriendRequestRepository friendRequestRepository) {
        this.friendRequestRepository = friendRequestRepository;
    }

    /**
     * Sends a friend request from the sender to the receiver by persisting a new FriendRequest object
     */
    public void sendRequest(FriendRequest friendRequest) {

        // Only send the request if the sender has not already sent a request to the receiver
        if (!friendRequestRepository.hasRequestSent(friendRequest.getSender(), friendRequest.getReceiver())) {
            friendRequestRepository.save(friendRequest);
        }
    }

    /**
     * Cancels a friend request by deleting the FriendRequest object from the database
     * @param sender the user who sent the request
     * @param userToCancel the user who received the request
     */
    public void cancelRequest(User sender, User userToCancel) {
        friendRequestRepository.deleteBySender(sender, userToCancel);
    }
}
