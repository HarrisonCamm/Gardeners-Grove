package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendRequestService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ManageFriendsController {

    Logger logger = LoggerFactory.getLogger(ManageFriendsController.class);

    private UserService userService;
    private FriendRequestService friendRequestService;

    @Autowired
    public ManageFriendsController(UserService userService, FriendRequestService friendRequestService) {
        this.userService = userService;
        this.friendRequestService = friendRequestService;
    }




    @GetMapping("/manage-friends")
    public String getManageFriends(Model model) {

        logger.info("GET /manage-friends");

        RedirectService.addEndpoint("/manage-friends");


        model.addAttribute("showSearch", true);

        return "manageFriendsTemplate";
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

        if (action.equals("search")) {
            return handleSearchRequest(searchQuery, model);
        } else if (action.equals("invite")) {
            return handleInviteRequest(email, model);
        }
        return "";
    }

    /**
     * Handles the search request
     * @param searchQuery the search query to search for users
     * @param model the model to add attributes to
     * @return the template to be rendered
     */
    private String handleSearchRequest(String searchQuery, Model model) {
        logger.info("POST /manage-friends (search)");

        List<User> searchedUsers = userService.searchForUsers(searchQuery.toLowerCase());

        if (searchedUsers.isEmpty()) {
            model.addAttribute("searchResultMessage",
                    "Nobody with that name or email in Gardener’s Grove");
        }

        model.addAttribute("matchedUsers", searchedUsers);
        model.addAttribute("showSearch", true);
        model.addAttribute("searchQuery", searchQuery);

        return "manageFriendsTemplate";
    }

    /**
     * Handles the invite request
     * @param email the email of the user to be invited
     * @param model the model to add attributes to
     * @return the template to be rendered
     */
    private String handleInviteRequest(String email, Model model) {
        logger.info("POST /manage-friends (invite)");

        User currentUser = userService.getAuthenicatedUser();
        User invitedUser = userService.getUserByEmail(email);

        friendRequestService.sendRequest(currentUser, invitedUser);

        return "manageFriendsTemplate";
    }


}
