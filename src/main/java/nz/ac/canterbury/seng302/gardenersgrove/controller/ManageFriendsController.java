package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageFriendsController {

    Logger logger = LoggerFactory.getLogger(ManageFriendsController.class);



    @GetMapping("/manage-friends")
    public String getManageFriends(Model model) {

        logger.info("GET /manage-friends");

        RedirectService.addEndpoint("/manage-friends");

//        model.addAttribute("searchQuery", "");

        model.addAttribute("showSearch", true);

        return "manageFriendsTemplate";
    }

    @PostMapping("/manage-friends")
    public String searchForFriends(@RequestParam(name="searchQuery") String searchQuery,
                                   Model model) {

        logger.info("POST /manage-friends");

        model.addAttribute("showSearch", true);

        model.addAttribute("searchQuery", searchQuery);

        return "manageFriendsTemplate";
    }

}
