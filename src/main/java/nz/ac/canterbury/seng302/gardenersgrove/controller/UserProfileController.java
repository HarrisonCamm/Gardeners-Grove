package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller for viewing the user profile page
 */
@Controller
public class UserProfileController {

    Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserService userService;

    private final UserRepository userRepository;
    private final ImageService imageService;

    @Autowired
    public UserProfileController(UserService newUserService, UserRepository newUserRepository, ImageService imageService) {
        this.userService = newUserService;
        this.userRepository = newUserRepository;
        this.imageService = imageService;
    }

    /**
     * Gets the thymeleaf page showing the user profile of the logged-in user
     *
     * @param session The http session
     * @param model The model
     */
    @GetMapping("/view-user-profile")
    public String getTemplate(HttpSession session, Model model) {
        RedirectService.addEndpoint("/view-user-profile");
        User currentUser = userService.getAuthenicatedUser();

        logger.info("User retrieved from session: " + currentUser);

        Image.removeTemporaryImage(session, imageService);
        session.removeAttribute("imageFile");

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

    /**
     * Handles saving a new user profile image that was uploaded from the View User Profile page
     *
     * @param userID The id of the user
     * @param file The image file which can be reread if necessary
     * @param session The http session
     * @param model The model
     * @return A redirect to this same page to refresh it
     * @throws IOException If the file cannot be read
     */
    @PostMapping("/view-user-profile")
    public String uploadImage(@RequestParam(value = "userID") Long userID,
                              @RequestParam(value = "file") MultipartFile file,
                              HttpSession session,
                              Model model) throws IOException {
        Optional<User> user = userRepository.findById(userID);
        if (user.isPresent()) {
            User userToEdit = user.get();
            Image image = Image.removeTemporaryImage(session, imageService);
            image = (image == null ? new Image(file, false) : image.makePermanent());

            Image oldImage = userToEdit.getImage();
            userToEdit.setImage(image);
            userRepository.save(userToEdit);
            if (oldImage != null) {
                imageService.deleteImage(oldImage);
            }
        }
        return "redirect:/view-user-profile";
    }
}
