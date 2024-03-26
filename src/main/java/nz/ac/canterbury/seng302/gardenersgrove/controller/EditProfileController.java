package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
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
        model.addAttribute("changePasswordForm", false); // Hide the change password form initially
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
                             @RequestParam(name="changePasswordForm" , required = false) boolean changePasswordForm,
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

        // Setting model attributes to be persistent in the form instead of clearing
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("noLastName", noLastName);
        model.addAttribute("changePasswordForm", changePasswordForm);
        model.addAttribute("email", email);
        model.addAttribute("dateOfBirth", dateOfBirth);

        logger.info(String.valueOf(changePasswordForm));

        // Check if the date of birth is empty or null
        String formattedDateOfBirth;
        if (dateOfBirth == null || dateOfBirth.isEmpty()) {
            formattedDateOfBirth = "";
        } else {
            formattedDateOfBirth = convertDateFormat(dateOfBirth);
        }

        // Begin Validation

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
                || model.containsAttribute("lastNameError") || model.containsAttribute("ageError")) {
            return "editUserProfileTemplate";
        } else {
            // Email has not been used
            currentUser = userService.updateUser(currentUser, firstName, lastName, noLastName, email, dateOfBirth);
            model.addAttribute("displayName", firstName + " " + lastName);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(currentUser.getEmail(), currentUser.getPassword(), currentUser.getAuthorities());
            // Authenticate the token properly with the CustomAuthenticationProvider
            Authentication authenticationToken = authenticationManager.authenticate(token);
            // Check if the authentication is actually authenticated (in this example any username/password is accepted so this should never be false)
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
}
