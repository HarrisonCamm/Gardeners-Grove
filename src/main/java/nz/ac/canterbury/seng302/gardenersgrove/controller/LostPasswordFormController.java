package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for reset password  form.
 * Note the @link{Autowired} annotation giving us access to the @link{FormService} class automatically
 */

@Controller
public class LostPasswordFormController {
    Logger logger = LoggerFactory.getLogger(RegisterFormController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public LostPasswordFormController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf lostPasswordFormTemplate
     */
    @GetMapping("/lost-password-form")
    public String form(@RequestParam(name = "email", required = false) String email,
                       Model model) {
        logger.info("GET /lost-password-form");
        model.addAttribute("email", "");
        return "lostPasswordFormTemplate";
    }

    /**
     * Posts a form response with email
     * @param email email of user to send reset link
     * @param model (map-like) representation for use in thymeleaf,
     *              with values being set to relevant parameters provided
     * @return thymeleaf lostPasswordFormTemplate and popup message
     */
    @PostMapping("/lost-password-form")
    public String submitForm(@RequestParam(name="email") String email,
                             HttpServletRequest request,
                             Model model) {
        logger.info("POST /lost-password-form");

        model.addAttribute("email", email);

        // Check if email already exists
//        if (userService.emailExists(email)) { // todo delete this?
//            model.addAttribute("lostPasswordEmailError", "This email address is already in use");
//        }
        if (email.isEmpty() || !isEmailValid(email)) {
            model.addAttribute("lostPasswordEmailError", "Email address must be in the form ‘jane@doe.nz’");
        }
        if (model.containsAttribute("lostPasswordEmailError")) {
            return "lostPasswordFormTemplate";
        } else {
            // Email has not been used / todo check this

            // todo send email
            return "redirect:/lost-password-form"; // todo implement popup confirmation message
        }
    }

    private boolean isEmailValid(String email) {
        // Regex to check for a specific email pattern
        boolean isValid = email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$") && !email.contains("..");

        // Check if the email exceeds the SQL limit
        boolean isWithinSqlLimit = email.length() <= 255;

        return isValid && isWithinSqlLimit;
    }
}
