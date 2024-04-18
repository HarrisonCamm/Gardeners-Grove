package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

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

    @Autowired
    public EditProfileController(UserService userService, UserRepository newUserRepository, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userRepository = newUserRepository;
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    private MailService mailService;


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
        model.addAttribute("changePasswordFormInput", false); // Hide the change password form initially
        model.addAttribute("dateOfBirth", currentUser.getDateOfBirth());

        return "editUserProfileTemplate";
    }

    /**
     * Posts a form response with name and favourite language
     * @param firstName first name if user
     * @param lastName last name if user
     * @param email email if user
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf,
     *              with values being set to relevant parameters provided
     * @return thymeleaf demoFormTemplate
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

        if (lastName == null) {
            lastName = "";
        }

        // Trim the names to remove leading and trailing white spaces
        firstName = firstName.trim();
        lastName = lastName.trim();

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
            if (oldPassword == null || oldPassword.isEmpty() || newPassword == null || newPassword.isEmpty() || retypePassword == null || retypePassword.isEmpty()) {
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
    }

    // User details validations
    // Check the date of birth and format it to empty string or dd/mm/yyyy
    String formattedDateOfBirth;
    if (dateOfBirth == null || dateOfBirth.isEmpty()) {
        formattedDateOfBirth = "";
    } else {
        formattedDateOfBirth = convertDateFormat(dateOfBirth);
    }

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
    if (model.containsAttribute("registrationEmailError") || model.containsAttribute("firstNameError")
            || model.containsAttribute("lastNameError") || model.containsAttribute("ageError")
            || model.containsAttribute("oldPasswordError") || model.containsAttribute("newPasswordError")
            || model.containsAttribute("passwordMatchError")) {
        return "editUserProfileTemplate";
    } else {
        // Email has not been used, update user details
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
            } catch (Exception e) {
                // Log the error
                logger.error("Failed to send password change confirmation email to " + emailAddress, e);
                // TODO display an error message
            }
        }


        model.addAttribute("displayName", firstName + " " + lastName);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(currentUser.getEmail(), currentUser.getPassword(), currentUser.getAuthorities());
        // Authenticate the token properly with the CustomAuthenticationProvider
        Authentication authenticationToken = authenticationManager.authenticate(token);
        // Check if the authentication is actually authenticated (any username/password is accepted so this should never be false)
        if(authenticationToken.isAuthenticated()) {
            // Add the authentication to the current security context (Stateful)
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            // Add the token to the request session (needed so the authentication can be properly used)
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        }

        return "redirect:/view-user-profile";
    }
}

    private boolean isNameValid(String name) {
        // Regex to check for a valid name including non-English characters
        boolean isValid = name.matches("[\\p{L}\\s'-]+");

        // Check if the name contains multiple consecutive spaces
        boolean hasMultipleSpaces = name.contains("  ");

        return isValid && !hasMultipleSpaces;
    }

    private boolean isEmailValid(String email) {
        // Regex to check for a specific email pattern
        boolean isValid = email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$") && !email.contains("..");

        // Check if the email exceeds the SQL limit
        boolean isWithinSqlLimit = email.length() <= 255;

        return isValid && isWithinSqlLimit;
    }

    public static boolean checkDateValidity(String dateOfBirth) {
        try {
            LocalDate dob = LocalDate.parse(dateOfBirth);
            return true;
            // Continue with further processing
        } catch (DateTimeParseException e) {
            return false;
            // Handle the case where the date string doesn't match the expected format
        }
    }

    public static int calculateAge(String dateOfBirth) {
        LocalDate dob = LocalDate.parse(dateOfBirth);
        LocalDate today = LocalDate.now();
        Period period = Period.between(dob, today);
        int age = period.getYears();
        if (period.getMonths() < 0 || (period.getMonths() == 0 && today.getDayOfMonth() < dob.getDayOfMonth())) {
            age--;
        }
        return age;
    }

    public static String convertDateFormat(String dateInput) {
        String[] parts = dateInput.split("/");
        if (dateInput.length() < 10) {
            return "0000-00-00";
        } else {
            // Reconstruct the date string in yyyy-MM-dd format
            String yyyy = parts[2];
            String mm = parts[1];
            String dd = parts[0];

            // Ensure mm and dd are formatted with leading zeros if necessary
            if (mm.length() == 1) {
                mm = "0" + mm;
            }
            if (dd.length() == 1) {
                dd = "0" + dd;
            }
            return yyyy + "-" + mm + "-" + dd;
        }
    }

    public static boolean isPasswordValid(String password) {
        String specialCharacters = "[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]+";
        return password.length() >= 8 &&
                password.matches(".*\\d.*") &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                Pattern.compile(specialCharacters).matcher(password).find();
    }
}
