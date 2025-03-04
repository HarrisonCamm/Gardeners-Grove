package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidator.*;

/**
 * Controller for registration form.
 * Note the @link{Autowired} annotation giving us access to the @link{FormService} class automatically
 */
@Controller
public class EditProfileController {
    Logger logger = LoggerFactory.getLogger(EditProfileController.class);

    private final UserService userService;
    private UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    private final ImageService imageService;
    private final ItemService itemService;


    @Autowired
    public EditProfileController(UserService userService, UserRepository newUserRepository,
                                 AuthenticationManager authenticationManager, MailService mailService,
                                 ImageService imageService, ItemService itemService) {
        this.userService = userService;
        this.userRepository = newUserRepository;
        this.authenticationManager = authenticationManager;
        this.mailService = mailService;
        this.imageService = imageService;
        this.itemService = itemService;
    }

    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     *
     * @param request previous name entered into form to be displayed
     * @param model   (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf demoFormTemplate
     */
    @GetMapping("/edit-user-profile")
    public String form(HttpSession session, HttpServletRequest request, Model model) {
        logger.info("GET /edit-user-profile");
        RedirectService.addEndpoint("/edit-user-profile");
        User currentUser = userService.getAuthenticatedUser();

        addModelAttributes(model, currentUser, currentUser.getFirstName(), currentUser.getLastName(), currentUser.getNoLastName(),
                currentUser.getEmail(), currentUser.getDateOfBirth(), false, true,
                null, null, null);
        logger.info("current user + "  + currentUser.getDateOfBirth());

        Image.removeTemporaryImage(session, imageService);
        session.removeAttribute("imageFile");

        return "editUserProfileTemplate";
    }

    /**
     * Handles the submission of the edit user profile form, but not the password subform.
     * This method processes form inputs for user profile updates, including personal information and password changes.
     * It performs validations on the provided inputs such as name validity, email format, and password criteria.
     * If any validation fails, it returns to the form with error messages; otherwise, it updates the user's information.
     *
     * @param firstName The first name of the user, mandatory.
     * @param lastName The last name of the user, optional.
     * @param noLastName A boolean flag to indicate if the user has no last name.
     * @param email The email address of the user, used for contact and login.
     * @param changePasswordFormInput A flag indicating whether the password change form was triggered.
     * @param oldPassword The user's current password, required for password change validation.
     * @param newPassword The user's new password to be set, must meet security criteria.
     * @param retypePassword The new password retyped for confirmation.
     * @param dateOfBirth The user's date of birth in the format DD/MM/YYYY.
     * @param model The Spring MVC model used to pass attributes to the view.
     * @param request The HttpServletRequest object, providing request information for HTTP servlets.
     * @return The name of the view to be rendered, depending on the result of the form submission.
     */
    @PostMapping("/edit-user-profile")
    public String submitForm(@RequestParam(name="firstName") String firstName,
                             @RequestParam(name="lastName", required=false) String lastName,
                             @RequestParam(name="noLastName", required=false) boolean noLastName,
                             @RequestParam(name="email") String email,
                             @RequestParam(name="changePasswordFormInput", required=false) boolean changePasswordFormInput,
                             @RequestParam(name="oldPassword", required = false) String oldPassword,
                             @RequestParam(name="newPassword", required = false) String newPassword,
                             @RequestParam(name="retypePassword", required = false) String retypePassword,
                             @RequestParam(name="dateOfBirth", required = false) String dateOfBirth,
                             Model model, HttpServletRequest request) {

        logger.info("POST /edit-user-profile");

        // Format first and last names
        firstName = formatName(firstName);
        lastName = formatName(lastName);

        User currentUser = userService.getAuthenticatedUser();

        logger.info("User retrieved from session: " + currentUser);
        // Format and convert the data of birth
        String formattedDateOfBirth;
        if (dateOfBirth.isEmpty()) {
            formattedDateOfBirth = "";
        } else {
            formattedDateOfBirth = convertDateFormat(dateOfBirth);
        }


        // Pre-populate the model with submitted values to persist them in case of an error
        addModelAttributes(model, currentUser, firstName, lastName, noLastName, email, dateOfBirth,
                changePasswordFormInput, true, oldPassword, newPassword, retypePassword);

        doUserValidations(model, userService, currentUser, email, noLastName, firstName, lastName, formattedDateOfBirth);

        // Check for errors, if error thrown display error message
        if (model.containsAttribute("registrationEmailError")
                || model.containsAttribute("firstNameError")
                || model.containsAttribute("lastNameError")
                || model.containsAttribute("ageError")) {
            return "editUserProfileTemplate";
        } else {
            // No errors, continue with updating user details
            currentUser = userService.updateUser(currentUser, firstName, lastName, noLastName, email, dateOfBirth);

            // Display the user's full name
            model.addAttribute("displayName", firstName + " " + lastName);

            renewAuthentication(currentUser);

            // Redirect to the user profile page after successful update of user details
            return "redirect:/view-user-profile";
        }
    }

    /**
     * Handles the change password nested form submission, and nothing of the outer form.
     * @param firstName The first name of the user, mandatory.
     * @param lastName The last name of the user, optional.
     * @param noLastName A boolean flag to indicate if the user has no last name.
     * @param email The email address of the user, used for contact and login.
     * @param changePasswordFormInput A flag indicating whether the password change form was triggered.
     * @param oldPassword The user's current password, required for password change validation.
     * @param newPassword The user's new password to be set, must meet security criteria.
     * @param retypePassword The new password retyped for confirmation.
     * @param dateOfBirth The user's date of birth in the format DD/MM/YYYY.
     * @param model The Spring MVC model used to pass attributes to the view.
     * @param request The HttpServletRequest object, providing request information for HTTP servlets.
     * @return The name of the view to be rendered, depending on the result of the form submission.
     */
    @PostMapping("/edit-user-profile-password")
    public String changePassword(@RequestParam(name="firstName") String firstName,
                                 @RequestParam(name="lastName", required=false) String lastName,
                                 @RequestParam(name="noLastName", required=false) boolean noLastName,
                                 @RequestParam(name="email") String email,
                                 @RequestParam(name="changePasswordFormInput", required=false) boolean changePasswordFormInput,
                                 @RequestParam(name="oldPassword", required = false) String oldPassword,
                                 @RequestParam(name="newPassword", required = false) String newPassword,
                                 @RequestParam(name="retypePassword", required = false) String retypePassword,
                                 @RequestParam(name="dateOfBirth", required = false) String dateOfBirth,
                                 Model model, HttpServletRequest request) {
        logger.info("POST /edit-user-profile-password");

        // Format first and last names
        firstName = formatName(firstName);
        lastName = formatName(lastName);

        User currentUser = userService.getAuthenticatedUser();

        // Pre-populate the model with submitted values to persist them in case of an error
        addModelAttributes(model, currentUser, firstName, lastName, noLastName, email, dateOfBirth,
                changePasswordFormInput, true, oldPassword, newPassword, retypePassword);

        doPasswordValidations(model, userService, currentUser, oldPassword, newPassword, retypePassword);

        // Check for errors, if error thrown display error message
        if (!((model.containsAttribute("oldPasswordError")
                || model.containsAttribute("newPasswordError")
                || model.containsAttribute("passwordMatchError")))) {

            userService.updateUserPassword(currentUser, newPassword);
            // send user confirmation email of password change
            String emailAddress = currentUser.getEmail();
            String emailSubject = "Password Change Confirmation";
            String emailText = "Dear " + currentUser.getFirstName() + ",\n\n" +
                    "Your password has been successfully updated. If you did not make this change, your account is at risk and you should contact s302team600@cosc.canterbury.ac.nz .\n\n" +
                    "Best,\n" +
                    "The Gardener's Grove Team";

            // Try to send the email
            try {
                mailService.sendSimpleMessage(emailAddress, emailSubject, emailText);
                // Close password form
                model.addAttribute("changePasswordFormInput", false);
            } catch (Exception e) {
                // Log the error
                final String errorMessage = "Failed to send password change confirmation email to " + emailAddress
                        + ". Please try again later.";
                logger.error(errorMessage, e);
                model.addAttribute("emailError", errorMessage);
            }

            renewAuthentication(currentUser);
        }
        return "editUserProfileTemplate";
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
    @PostMapping("/edit-user-profile-image")
    public String uploadImage(@RequestParam(value = "userID") Long userID,
                              @RequestParam(value = "file") MultipartFile file,
                              HttpSession session,
                              Model model) throws IOException {
        logger.info("POST /edit-user-profile-image");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Optional<User> user = userRepository.findById(userID);
        if (user.isPresent()) {
            User userToEdit = user.get();
            Image image = Image.removeTemporaryImage(session, imageService);
            image = (image == null ? new Image(file, false) : image.makePermanent());

            Image oldImage = userToEdit.getImage();
            userToEdit.setImage(image);
            userRepository.save(userToEdit);
            if (oldImage != null && itemService.getImageItemsByImageId(oldImage.getId()).isEmpty()) {
                imageService.deleteImage(oldImage);
            }

            // Retrieve the user from the database
            userToEdit = userService.getUserByID(userToEdit.getUserId());
            // init user with uploaded image id
            userToEdit.setUploadedImageId(userToEdit.getImage().getId());
            userService.saveUser(userToEdit);
        }

        return "redirect:/edit-user-profile";
    }

    /**
     * Adds the model attributes for the edit profile form
     */
    private void addModelAttributes(Model model, User currentUser, String firstName, String lastName, boolean noLastName,
                                    String email, String dateOfBirth, boolean changePasswordFormInput, boolean addDisplayName,
                                    String oldPassword, String newPassword, String retypePassword) {
        model.addAttribute("user", currentUser);
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("noLastName", noLastName);
        model.addAttribute("changePasswordFormInput", changePasswordFormInput);
        if (addDisplayName)
            model.addAttribute("displayName", firstName + " " + lastName);
        if (oldPassword != null)
            model.addAttribute("oldPassword", oldPassword);
        if (newPassword != null)
            model.addAttribute("newPassword", newPassword);
        if (retypePassword != null)
            model.addAttribute("retypePassword", retypePassword);
        model.addAttribute("email", email);
        model.addAttribute("dateOfBirth", dateOfBirth);
    }

    /**
     * Renews the authentication of the user
     *
     * @param user The user to renew the authentication for
     */
    private void renewAuthentication(User user) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), user.getAuthorities());
        // Authenticate the token properly with the CustomAuthenticationProvider
        Authentication authenticationToken = authenticationManager.authenticate(token);
        // Check if the authentication is actually authenticated (any username/password is accepted, so this should never be false)
        if (authenticationToken.isAuthenticated()) {
            // Add the authentication to the current security context (Stateful)
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }
}
