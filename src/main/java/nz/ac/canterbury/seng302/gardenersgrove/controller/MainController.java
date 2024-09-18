package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller for viewing the main page, mostly blank for now
 */

@Controller
public class MainController {

    Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private UserService userService;

    @Autowired
    public MainController(UserService newUserService) {
        this.userService = newUserService;
    }

    /**
     * Gets the thymeleaf page showing the main page
     */
    @GetMapping("/main")
    public String getTemplate(HttpServletRequest request,
                              @RequestParam(name="name", required = false, defaultValue = "") String name,
                              Model model) {
        User currentUser = userService.getAuthenticatedUser();
        logger.info("GET /main");
        RedirectService.addEndpoint("/main");
        List<User> topUsers = userService.getTop10UsersByBloomBalance();

        model.addAttribute("topUsers", topUsers);
        model.addAttribute("name", name);
        model.addAttribute("user", currentUser);
        model.addAttribute("userRank", userService.getUserRank(currentUser.getUserId()));
        return "mainTemplate";
    }
}
