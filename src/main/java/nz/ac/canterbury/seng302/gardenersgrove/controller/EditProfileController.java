package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.MailService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;
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

    @Autowired
    public EditProfileController(UserService userService, UserRepository newUserRepository, AuthenticationManager authenticationManager, MailService mailService) {
        this.userService = userService;
        this.userRepository = newUserRepository;
        this.authenticationManager = authenticationManager;
        this.mailService = mailService;
    }

    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     *
     * @param request previous name entered into form to be displayed
     * @param model   (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf demoFormTemplate
     */
    @GetMapping("/edit-user-profile")
    public String form(HttpServletRequest request, Model model) {

        logger.info("GET /edit-user-profile");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User currentUser = userService.getUserByEmail(currentPrincipalName);

        model.addAttribute("displayName", (currentUser.getFirstName() + " " + currentUser.getLastName()));
        model.addAttribute("firstName", currentUser.getFirstName());
        model.addAttribute("lastName", currentUser.getLastName());
        model.addAttribute("noLastName", currentUser.getNoLastName());
        model.addAttribute("email", currentUser.getEmail());
        model.addAttribute("changePasswordFormInput", false);
        model.addAttribute("dateOfBirth", currentUser.getDateOfBirth());

        return "editUserProfileTemplate";
    }

    /**
     * Handles the submission of the edit user profile form.
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User currentUser = userService.getUserByEmail(currentPrincipalName);

        logger.info("User retrieved from session: " + currentUser);

        // Pre-populate the model with submitted values to persist them in case of an error
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("noLastName", noLastName);
        model.addAttribute("changePasswordFormInput", changePasswordFormInput);
        model.addAttribute("oldPassword", oldPassword);
        model.addAttribute("newPassword", newPassword);
        model.addAttribute("retypePassword", retypePassword);
        model.addAttribute("email", email);
        model.addAttribute("dateOfBirth", dateOfBirth);

        // Begin Validations

        // Change Password Validations
        if (changePasswordFormInput) {
            // Only perform password validation when change password form open (changePasswordFormInput == true)

            // Check if the old password is empty
            if (oldPassword == null || oldPassword.isEmpty()) {
                model.addAttribute("oldPasswordError", "Old password is required.");
            } else {
                // Attempt to validate the user with the provided old password
                Optional<User> validatedUser = userService.validateUser(currentUser.getEmail(), oldPassword);

                // If the Optional is empty, the old password does not match
                if (!validatedUser.isPresent()) {
                    model.addAttribute("oldPasswordError", "Your old password is incorrect");
                }
            }

            // Check if the new password is empty
            if (newPassword == null || newPassword.isEmpty()) {
                model.addAttribute("newPasswordError", "New password is required.");
            } else {
                // Validate the new password strength
                if (!isPasswordValid(newPassword)) {
                    model.addAttribute("newPasswordError", "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.");
                }
            }

            // Check if the retyped password is empty
            if (retypePassword == null || retypePassword.isEmpty()) {
                model.addAttribute("passwordMatchError", "Retyping the new password is required.");
            } else {
                // Check if the new password and retype password match
                if (!newPassword.equals(retypePassword)) {
                    model.addAttribute("passwordMatchError", "The new passwords do not match");
                }
            }
        }

    // Format and convert the data of birth
    String formattedDateOfBirth = convertDateFormat(dateOfBirth);

    // Begin User Details Validations

    // Check if email already exists
    if (userService.emailExists(email) && !Objects.equals(email, currentUser.getEmail())) {
        model.addAttribute("registrationEmailError", "This email address is already in use");
    }
    if (email.isEmpty() ||!isEmailValid(email)){
        model.addAttribute("registrationEmailError", "Email address must be in the form ‘jane@doe.nz’");
    }
    if (firstName.length() > 64) {
        model.addAttribute("firstNameError", "First name must be 64 characters long or less");
    }
    if (!isNameValid(firstName)) {
        model.addAttribute("firstNameError", "First name must only include letters, spaces, hyphens or apostrophes");
    }
    if (firstName.isEmpty()) {
        model.addAttribute("firstNameError", "First name cannot be empty");
    }
    if (lastName.length() > 64) {
        model.addAttribute("lastNameError", "Last name must be 64 characters long or less");
    }
    if (!isNameValid(lastName) && !lastName.isEmpty()) {
        model.addAttribute("lastNameError", "Last name must only include letters, spaces, hyphens or apostrophes");
    }
    if (!noLastName && lastName.isEmpty()) {
        model.addAttribute("lastNameError", "Last name cannot be empty");
    }
    if (!formattedDateOfBirth.isEmpty() && !checkDateValidity(formattedDateOfBirth)) {
        model.addAttribute("ageError", "Date in not in valid format, DD/MM/YYYY");
    }
    if (!formattedDateOfBirth.isEmpty() && checkDateValidity(formattedDateOfBirth) && calculateAge(formattedDateOfBirth) < 13) {
        model.addAttribute("ageError", "You must be 13 years old or older to create an account");
    }
    if (!formattedDateOfBirth.isEmpty() && checkDateValidity(formattedDateOfBirth) && calculateAge(formattedDateOfBirth) > 120) {
        model.addAttribute("ageError", "The maximum age allowed is 120 years");
    }

    // Check for errors, if error thrown display error message
    if (model.containsAttribute("registrationEmailError") || model.containsAttribute("firstNameError")
            || model.containsAttribute("lastNameError") || model.containsAttribute("ageError")
            || model.containsAttribute("oldPasswordError") || model.containsAttribute("newPasswordError")
            || model.containsAttribute("passwordMatchError")) {
        return "editUserProfileTemplate";
    } else {
        // No errors, continue with updating user details
        currentUser = userService.updateUser(currentUser, firstName, lastName, noLastName, email, dateOfBirth);

        // If the change password form is open, and the password fields are valid (which they are if reaching this stage), update the password
        if (changePasswordFormInput) {
            userService.updateUserPassword(currentUser, newPassword);
            // send user confirmation email of password change
            String emailAddress = currentUser.getEmail();
            String emailSubject = "Password Change Confirmation";
            String emailText = "Dear " + currentUser.getFirstName() + ",\n\n" +
                    "Your password has been successfully updated. If you did not make this change, please contact support immediately.\n\n" +
                    "Best,\n" +
                    "The Gardener's Grove Team";

            // Try to send the email
            try {
                mailService.sendSimpleMessage(emailAddress, emailSubject, emailText);
                // Close password form
                model.addAttribute("changePasswordFormInput", false);
                // Password updated, allow user to continue to edit other details
                return "editUserProfileTemplate";
            } catch (Exception e) {
                // Log the error
                logger.error("Failed to send password change confirmation email to " + emailAddress, e);
                // TODO display an error message
            }
        }

        // Display the user's full name
        model.addAttribute("displayName", firstName + " " + lastName);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(currentUser.getEmail(), currentUser.getPassword(), currentUser.getAuthorities());
        // Authenticate the token properly with the CustomAuthenticationProvider
        Authentication authenticationToken = authenticationManager.authenticate(token);
        // Check if the authentication is actually authenticated (any username/password is accepted, so this should never be false)
        if(authenticationToken.isAuthenticated()) {
            // Add the authentication to the current security context (Stateful)
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            // Add the token to the request session (needed so the authentication can be properly used)
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        }
        // Redirect to the user profile page after successful update of user details
        return "redirect:/view-user-profile";
    }
    }
}
