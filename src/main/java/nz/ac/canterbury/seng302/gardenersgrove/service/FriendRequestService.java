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
    public FriendRequest sendRequest(FriendRequest friendRequest) {
        if (friendRequestRepository.hasRequestSent(friendRequest.getSender(), friendRequest.getReceiver())) {
            return null;
        } else {
            return friendRequestRepository.save(friendRequest);
        }

    }
}
