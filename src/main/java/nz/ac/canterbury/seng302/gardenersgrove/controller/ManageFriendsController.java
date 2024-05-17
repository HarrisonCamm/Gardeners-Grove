package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
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

    @Autowired
    public ManageFriendsController(UserService userService) {
        this.userService = userService;
    }




    @GetMapping("/manage-friends")
    public String getManageFriends(Model model) {

        logger.info("GET /manage-friends");

        RedirectService.addEndpoint("/manage-friends");


        model.addAttribute("showSearch", true);

        return "manageFriendsTemplate";
    }

    @PostMapping("/manage-friends")
    public String searchForFriends(@RequestParam(name="searchQuery") String searchQuery,
                                   Model model) {

        logger.info("POST /manage-friends");

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

}
