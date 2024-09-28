package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Transaction;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TransactionService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Date;
import java.util.Optional;

/**
 * Controller for viewing the user profile page
 */
@Controller
public class UserProfileController {

    private static final Integer PAGE_SIZE = 10;
    Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;



    private final UserRepository userRepository;
    private final ImageService imageService;

    @Autowired
    public UserProfileController(UserService newUserService, UserRepository newUserRepository, ImageService imageService, TransactionService transactionService) {
        this.userService = newUserService;
        this.userRepository = newUserRepository;
        this.imageService = imageService;
        this.transactionService = transactionService;
    }

    /**
     * Gets the thymeleaf page showing the user profile of the logged-in user
     *
     * @param session The http session
     * @param model The model
     */
    @GetMapping("/view-user-profile")
    public String getTemplate( @RequestParam(defaultValue = "0") Integer page,
                               HttpSession session, Model model) {
        RedirectService.addEndpoint("/view-user-profile");
        User currentUser = userService.getAuthenticatedUser();

        Page<Transaction> transactionsPage = transactionService.findTransactionsByUser(currentUser, page, PAGE_SIZE);

        logger.info("User retrieved from session: " + currentUser);

        Image.removeTemporaryImage(session, imageService);
        session.removeAttribute("imageFile");

        if (currentUser != null) {
            model.addAttribute("user", currentUser);
            model.addAttribute("displayName", (currentUser.getFirstName() + " " + currentUser.getLastName()));
            model.addAttribute("email", currentUser.getEmail());
            model.addAttribute("dateOfBirth", currentUser.getDateOfBirth());

            if(transactionsPage != null) {
                model.addAttribute("transactions", transactionsPage.getContent());
                model.addAttribute("totalPages", transactionsPage.getTotalPages());
                model.addAttribute("hasPrevious", transactionsPage.hasPrevious());
                model.addAttribute("hasNext", transactionsPage.hasNext());
            } else {
                model.addAttribute("transactions", null);
                model.addAttribute("totalPages", 0);
                model.addAttribute("hasPrevious", false);
                model.addAttribute("hasNext", false);
            }

            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", PAGE_SIZE);

            //For the purposes of tests
            model.addAttribute("noTransactionsText", "No Transactions to Display");
            model.addAttribute("earnBloomsText", "You can earn Blooms by: Selling plants, playing games, receiving tips from other users");
            model.addAttribute("spendBloomsText", "You can spend Blooms by: Tipping other people's gardens, playing games, buying plants for your gardens");

            return "viewUserProfileTemplate";
        } else {
            model.addAttribute("signInError", "Please sign in to view your profile");
            return "redirect:/sign-in-form";
        }
    }


    @PostMapping("/transactions/add")
    public String addTransaction(@RequestParam int amount,
                                 @RequestParam String notes,
                                 @RequestParam String transactionType,
                                 @RequestParam Long receiverId,
                                 @RequestParam(required = false) Long senderId,
                                 @RequestParam(required = false) Long plantId) {
        transactionService.addTransaction(amount, notes, transactionType, receiverId, senderId, plantId);
        return "redirect:/transactions";
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

            // Retrieve the user from the database
            userToEdit = userService.getUserByID(userToEdit.getUserId());
            // init user with uploaded image id
            userToEdit.setUploadedImageId(userToEdit.getImage().getId());
            userService.saveUser(userToEdit);
        }
        return "redirect:/view-user-profile";
    }
}
