package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.entity.FriendRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.entity.UserRelationship;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendRequestService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserRelationshipService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ManageFriendsController {

    Logger logger = LoggerFactory.getLogger(ManageFriendsController.class);

    private final UserService userService;
    private final FriendRequestService friendRequestService;
    private final UserRelationshipService userRelationshipService;
    private boolean showSearch = true;
    public static final String DECLINED = "Declined";
    public static final String ACCEPTED = "Accepted";


    @Autowired
    public ManageFriendsController(UserService userService, FriendRequestService friendRequestService, UserRelationshipService userRelationshipService) {
        this.userService = userService;
        this.friendRequestService = friendRequestService;
        this.userRelationshipService = userRelationshipService;
    }




    @GetMapping("/manage-friends")
    public String getManageFriends(Model model) {

        logger.info("GET /manage-friends");

        RedirectService.addEndpoint("/manage-friends");

        User currentUser = userService.getAuthenticatedUser();

        model.addAttribute("removeFriendButton", true);

        showSearch = true;
        
        return addAttributes(model, currentUser);
    }

    /**
     * Handles page post mappings for searching for users and inviting users to be friends
     * @param action the action to be performed (search or invite)
     * @param email the email of the user to be invited
     * @param searchQuery the search query to search for users
     * @param model the model to add attributes to
     * @return the template to be rendered
     */
    @PostMapping("/manage-friends")
    public String handlePostRequest(@RequestParam(name="action") String action,
                                    @RequestParam(name="email", required = false) String email,
                                    @RequestParam(name="searchQuery", required = false) String searchQuery,
                                    Model model) {

        return switch (action) {
            case "search" -> handleSearchRequest(searchQuery, model);
            case "invite" -> handleInviteRequest(email, model);
            case "cancel" -> handleCancelRequest(email, model);
            case "accept" -> handleAcceptRequest(email);
            case "delete" -> handleRejectRequest(email, model);
            case "remove" -> handleRemoveRequest(email, model);
            default -> "manageFriendsTemplate"; // Should never reach this

        };
    }

    /**
     * Handles the search request
     * @param searchQuery the search query to search for users
     * @param model the model to add attributes to
     * @return the template to be rendered
     */
    private String handleSearchRequest(String searchQuery, Model model) {
        logger.info("POST /manage-friends (search)");

        User currentUser = userService.getAuthenticatedUser();

        List<User> searchedUsers = new ArrayList<>(userService.searchForUsers(searchQuery.toLowerCase(), currentUser));

        List<FriendRequest> sentFriendRequests = userService.getSentFriendRequests(currentUser);

        List<UserRelationship> usersRelationships = userRelationshipService.getUserRelationships(currentUser);


        for (FriendRequest request : sentFriendRequests) {
            searchedUsers.remove(request.getReceiver());
        }

        for (UserRelationship relationship: usersRelationships) {
            if (relationship.getStatus().equals(DECLINED)) {
                searchedUsers.remove(relationship.getReceiver());
            }
        }

        if (searchedUsers.isEmpty()) {
            model.addAttribute("searchResultMessage",
                    "There is nobody with that name or email in Gardener’s Grove");
        } else {
            model.addAttribute("matchedUsers", searchedUsers);
        }

        model.addAttribute("searchQuery", searchQuery);

        showSearch = false; // We want the results to be shown to the user

        return addAttributes(model, currentUser);
    }

    /**
     * Handles the invite request
     * @param email the email of the user to be invited
     * @param model the model to add attributes to
     * @return the template to be rendered
     */
    private String handleInviteRequest(String email, Model model) {
        logger.info("POST /manage-friends (invite)");

        User currentUser = userService.getAuthenticatedUser();
        User invitedUser = userService.getUserByEmail(email);

        FriendRequest friendRequest = new FriendRequest(currentUser, invitedUser);
        friendRequestService.save(friendRequest);
        friendRequestService.cancelRequest(invitedUser, currentUser);

        return "redirect:/manage-friends";
    }

    /**
     * Handles the cancel request
     * @param email the email of the user to be canceled
     * @param model the model to add attributes to
     * @return the template to be rendered
     */
    public String handleCancelRequest(String email, Model model) {
        logger.info("POST /manage-friends (cancel)");

        User currentUser = userService.getAuthenticatedUser();
        User canceledUser = userService.getUserByEmail(email);

        friendRequestService.cancelRequest(currentUser, canceledUser);

        return "redirect:/manage-friends";

    }

    /**
     * Handles the reject request
     * @param email the email of the user to be rejected
     * @param model the model to add attributes to
     * @return the template to be rendered
     */
    private String handleRejectRequest(String email, Model model) {
        logger.info("POST /manage-friends (reject)");

        User currentUser = userService.getAuthenticatedUser();
        User rejectedUser = userService.getUserByEmail(email);

        friendRequestService.rejectRequest(currentUser, rejectedUser);
        UserRelationship userRelationship = new UserRelationship(rejectedUser, currentUser, DECLINED);
        userRelationshipService.save(userRelationship);

        return "redirect:/manage-friends";
    }


    /**
     * Handles the accept request
     * @param email the email of the user to be accepted
     * @return the template to be rendered
     */
    private String handleAcceptRequest(String email) {
        logger.info("POST /manage-friends (accept)");

        // Check if the friend request is there first

        if (!friendRequestService.hasReceivedRequest(userService.getAuthenicatedUser(), userService.getUserByEmail(email))) {
            return "redirect:/manage-friends";
        }

        User currentUser = userService.getAuthenticatedUser();
        User acceptedFriend = userService.getUserByEmail(email);

        // Remove the request from the database, notice the order of the params
        // Removes duplicate requests.
        friendRequestService.cancelRequest(acceptedFriend, currentUser);
        friendRequestService.cancelRequest(currentUser, acceptedFriend);


        UserRelationship relationship = userRelationshipService.getRelationship(acceptedFriend, currentUser);
        if (relationship != null) {
            relationship.setStatus(ACCEPTED);
        } else {
            UserRelationship userRelationship = new UserRelationship( acceptedFriend, currentUser,ACCEPTED);
            userRelationshipService.save(userRelationship);
        }

        // Add each user to the other's friends list
        if (!currentUser.getFriends().contains(acceptedFriend)) {
            currentUser.addFriend(acceptedFriend);
            userService.updateUserFriends(currentUser);
        }
        if (!acceptedFriend.getFriends().contains(currentUser)) {
            acceptedFriend.addFriend(currentUser);
            userService.updateUserFriends(acceptedFriend);
        }
        return "redirect:/manage-friends";
    }


    /**
     * Handles the request to remove a friend.
     * @param email the email of the user to be removed as a friend
     * @param model the model to add attributes to
     * @return the template to be rendered
     */
    private String handleRemoveRequest(String email, Model model) {
        logger.info("POST /manage-friends (remove)");

        User currentUser = userService.getAuthenticatedUser();
        User friendToRemove = userService.getUserByEmail(email);
        UserRelationship relationship;
        if (userRelationshipService.getRelationship(currentUser, friendToRemove) != null) {
            relationship = userRelationshipService.getRelationship(currentUser, friendToRemove);
        } else {
            relationship = userRelationshipService.getRelationship(friendToRemove, currentUser);
        }
        userRelationshipService.remove(relationship);

        // Check if the user to remove is indeed a friend
        if (currentUser.getFriends().contains(friendToRemove)) {
            // Remove the user from the current user's friend list
            currentUser.removeFriend(friendToRemove);
            userService.updateUserFriends(currentUser);

            // Optionally, remove the current user from the friendToRemove's friend list
            friendToRemove.removeFriend(currentUser);
            userService.updateUserFriends(friendToRemove);

            model.addAttribute("removeMessage", "Successfully removed " + friendToRemove.getFirstName() + " from your friends list.");
        } else {
            model.addAttribute("removeMessage", "No such friend found in your friends list.");
        }

        return "redirect:/manage-friends";
    }


    private String addAttributes(Model model, User currentUser) {
        List<FriendRequest> sentFriendRequests = userService.getSentFriendRequests(currentUser);
        List<FriendRequest> pendingFriendRequests = userService.getPendingFriendRequests(currentUser);
        List<User> friends = currentUser.getFriends();

        model.addAttribute("pendingRequests", pendingFriendRequests);
        model.addAttribute("sentRequests", sentFriendRequests);
        model.addAttribute("friends", friends);
        model.addAttribute("showSearch", showSearch);

        return "manageFriendsTemplate";
    }


}
