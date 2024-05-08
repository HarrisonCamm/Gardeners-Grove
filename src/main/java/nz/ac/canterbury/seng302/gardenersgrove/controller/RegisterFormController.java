package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.entity.VerificationToken;
import nz.ac.canterbury.seng302.gardenersgrove.service.MailService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.VerificationTokenService;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Controller for registration form.
 * Note the @link{Autowired} annotation giving us access to the @link{FormService} class automatically
 */
@Controller
public class RegisterFormController {
    Logger logger = LoggerFactory.getLogger(RegisterFormController.class);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenService verificationTokenService;
    private final MailService mailService;

    @Autowired
    public RegisterFormController(UserService userService,
                                  AuthenticationManager authenticationManager,
                                  VerificationTokenService verificationTokenService,
                                  MailService mailService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.verificationTokenService = verificationTokenService;
        this.mailService = mailService;
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
            // All user details have passed validation

            // Create the user
            User newUser = new User(firstName, lastName, noLastName, email, password, dateOfBirth,  "defaultUserImage.png");
            // Create new user

            Path imagePath = Paths.get("src/main/resources/static/images/defaultUserImage.png");
            try {
                newUser.setImage(Files.readAllBytes(imagePath));
            } catch (IOException e) {
                logger.error("Failed to set default image", e);
            }

            // Save the user to database
            userService.addUser(newUser);

            // Grant user unverified role
            newUser.grantAuthority("ROLE_UNVERIFIED");

            // Auto-login security stuff
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(newUser.getEmail(), newUser.getPassword());
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            // Set the authenticated user in the session
            request.getSession().setAttribute("user", newUser);

            // Create Verification Token
            VerificationToken verificationToken = verificationTokenService.createVerificationToken(newUser);

            // Try to send confirmation email
            try {
                // Create confirmation email
                String emailSubject = "Your Account Registration Code";
                String emailText = "Dear " + firstName + ",\n\n" +
                        "Thank you for choosing to join Gardener's Grove! To complete your registration, please use the following code:\n\n" +
                        verificationToken.getToken() + "\n\n" +
                        "Please enter this code in the registration form to activate your account.\n\n" +
                        "If this was not you, you can ignore this message and the account will be deleted after 10 minutes.\n\n" +
                        "Welcome to Gardener's Grove! Happy gardening!";

                // Send confirmation email
                mailService.sendSimpleMessage(email, emailSubject, emailText);

            } catch (Exception e) {
                // Log the error
                logger.error("Failed to send confirmation code to " + email, e);
                // TODO display an error message

                // Keep user on registration page
                return "registerFormTemplate";
            }
            // Email sent successfully, confirm user registration page
            return "redirect:/confirm-registration";
        }
    }
}
