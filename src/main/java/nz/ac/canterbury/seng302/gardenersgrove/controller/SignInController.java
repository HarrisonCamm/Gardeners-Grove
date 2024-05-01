package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.VerificationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * This is a basic spring boot controller, note the @link{Controller} annotation which defines this.
 * This controller defines endpoints as functions with specific HTTP mappings
 */
@Controller
public class SignInController {
    Logger logger = LoggerFactory.getLogger(SignInController.class);

    private UserService userService;

    private UserRepository userRepository;

    private AuthenticationManager authenticationManager;

    private VerificationTokenService verificationTokenService;

    @Autowired
    public SignInController(UserService userService, UserRepository newUserRepository, AuthenticationManager authenticationManager, VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.userRepository = newUserRepository;
        this.authenticationManager = authenticationManager;
        this.verificationTokenService = verificationTokenService;
    }

    /**
     * Gets the thymeleaf page representing the /sign-in page (a basic welcome screen with some links)
     * @param model (map-like) representation of data to be used in thymeleaf display
     * @return thymeleaf demoTemplate
     */
    @GetMapping("/sign-in-form")
    public String getTemplate(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "token", required = false) String token,
                              Model model) {
        logger.info("GET /sign-in-form");
        if (error != null) {
            model.addAttribute("signInError", "The email address is unknown, or the password is invalid");
        }
        // Pass the email value back to the template
        model.addAttribute("email", "");
        model.addAttribute("message", "");
        model.addAttribute("token", "");

        String expiredTokenMessage = "Reset link has expired.";
        if (!verificationTokenService.validateToken(token) && token != null) { //show error on sign in page if reset pw token link expired
            model.addAttribute("expiredTokenError", expiredTokenMessage);
            logger.info("token is invalid on sign in form");
        }
        return "signInTemplate";
    }

}
