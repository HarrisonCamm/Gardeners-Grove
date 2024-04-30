package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Controller for viewing the user profile page
 */
@Controller
public class UserProfileController {

    Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserService userService;

    private UserRepository userRepository;

    @Autowired
    public UserProfileController(UserService newUserService, UserRepository newUserRepository) {
        this.userService = newUserService;
        this.userRepository = newUserRepository;

    }

    /**
     * Gets the thymeleaf page showing the user profile of the logged-in user
     */
    @GetMapping("/view-user-profile")
    public String getTemplate(Model model) {

//        User currentUser = (User) request.getSession().getAttribute("user");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User currentUser = userService.getUserByEmail(currentPrincipalName);

        logger.info("User retrieved from session: " + currentUser);

        if (currentUser != null) {
            model.addAttribute("user", currentUser);
            model.addAttribute("displayName", (currentUser.getFirstName() + " " + currentUser.getLastName()));
            model.addAttribute("email", currentUser.getEmail());
            model.addAttribute("dateOfBirth", currentUser.getDateOfBirth());
            return "viewUserProfileTemplate";
        } else {
            model.addAttribute("signInError", "Please sign in to view your profile");
            return "redirect:/sign-in-form";
        }
    }

    @PostMapping("/view-user-profile")
    public String uploadImage(@RequestParam(value = "file", required = false) String file,
                              @RequestParam(value = "userID", required = false) Long userID,
                              Model model) {
        Optional<User> user = userRepository.findById(userID);
        if (user.isPresent()) {
            User userToEdit = user.get();
            userToEdit.setImage(file);
            userToEdit.setFilePath("images/" + file);
            userRepository.save(userToEdit);
        }
        return "redirect:/view-user-profile";
    }
}
