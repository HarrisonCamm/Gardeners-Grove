package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidator.*;

/**
 * Controller for registration form.
 * Note the @link{Autowired} annotation giving us access to the @link{FormService} class automatically
 */
@Controller
public class RegisterFormController {
    Logger logger = LoggerFactory.getLogger(RegisterFormController.class);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public RegisterFormController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }
    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     * @param displayName previous name entered into form to be displayed
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf demoFormTemplate
     */
    @GetMapping("/register-form")
    public String form(@RequestParam(name="displayName", required = false, defaultValue = "") String displayName,
                       Model model) {
        logger.info("GET /register-form");
        model.addAttribute("displayName", displayName);
        return "registerFormTemplate";
    }

    /**
     * Posts a form response with name and favourite language
     * @param firstName first name if user
     * @param lastName last name if user
     * @param email email if user
     * @param password password if user
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf,
     *              with values being set to relevant parameters provided
     * @return thymeleaf demoFormTemplate
     */
    @PostMapping("/register-form")
    public String submitForm(@RequestParam(name="firstName") String firstName,
                             @RequestParam(name="lastName", required=false) String lastName,
                             @RequestParam(name="noLastName", required=false) boolean noLastName,
                             @RequestParam(name="email") String email,
                             @RequestParam(name="password") String password,
                             @RequestParam(name="password2") String password2,
                             @RequestParam(name="dateOfBirth", required = false) String dateOfBirth,
                             HttpServletRequest request,
                             Model model) {
        logger.info("POST /register-form");

        if (lastName == null) {
            lastName = "";
        }

        // Trim the names to remove leading and trailing white spaces
        firstName = firstName.trim();
        lastName = lastName.trim();

        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("noLastName", noLastName);
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        model.addAttribute("password2", password2);
        model.addAttribute("dateOfBirth", dateOfBirth);

        String formattedDateOfBirth;
        if (dateOfBirth.isEmpty()) {
            formattedDateOfBirth = "";
        } else {
            formattedDateOfBirth = convertDateFormat(dateOfBirth);
        }

        // Check if email already exists
        if (userService.emailExists(email)) {
            model.addAttribute("registrationEmailError", "This email address is already in use");
        }
        if (email.isEmpty() ||!isEmailValid(email)){
            model.addAttribute("registrationEmailError", "Email address must be in the form ‘jane@doe.nz’");
        }
        if (firstName.length() > 64) {
            model.addAttribute("firstNameError", "First name must be 64 characters long or less");
        }
        if (firstName.isEmpty() || !isNameValid(firstName)) {
            model.addAttribute("firstNameError", "First name cannot be empty and must only include letters, spaces, hyphens or apostrophes");
        }
        if (lastName.length() > 64) {
            model.addAttribute("lastNameError", "Last name must be 64 characters long or less");
        }
        if (!isNameValid(lastName) && !lastName.isEmpty()) {
            model.addAttribute("lastNameError", "Last name cannot be empty and must only include letters, spaces, hyphens or apostrophes");
        }
        if (!noLastName && lastName.isEmpty()) {
            model.addAttribute("lastNameError", "Last name cannot be empty and must only include letters, spaces, hyphens or apostrophes");
        }
        if (!isPasswordValid(password) || password.isEmpty()) {
            model.addAttribute("passwordValidityError", "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.");
        }
        if (!doPasswordsMatch(password, password2) || password2.isEmpty()) {
            model.addAttribute("passwordMatchError", "Passwords do not match");
        }
        if (!dateOfBirth.isEmpty() && !checkDateValidity(formattedDateOfBirth)) {
            model.addAttribute("ageError", "Date in not in valid format, DD/MM/YYYY");
        }
        if (!dateOfBirth.isEmpty() && checkDateValidity(formattedDateOfBirth) && calculateAge(formattedDateOfBirth) < 13) {
            model.addAttribute("ageError", "You must be 13 years old or older to create an account");
        }
        if (!dateOfBirth.isEmpty() && checkDateValidity(formattedDateOfBirth) && calculateAge(formattedDateOfBirth) > 120) {
            model.addAttribute("ageError", "The maximum age allowed is 120 years");
        }
        if (model.containsAttribute("registrationEmailError") || model.containsAttribute("firstNameError")
                || model.containsAttribute("lastNameError") || model.containsAttribute("passwordValidityError")
                || model.containsAttribute("passwordMatchError") || model.containsAttribute("ageError")) {
            return "registerFormTemplate";
        } else {
            // Email has not been used

            // Create new user
            User newUser = new User(firstName, lastName, noLastName, email, password, dateOfBirth);

            // Grant user role
            newUser.grantAuthority("ROLE_USER");

            // Register user
            userService.addUser(newUser);

            // Auto-login security stuff
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            // Set the authenticated user in the session
            request.getSession().setAttribute("user", newUser);

            model.addAttribute("displayName", firstName + " " + lastName);
            return "redirect:/view-user-profile";
        }
    }
}
