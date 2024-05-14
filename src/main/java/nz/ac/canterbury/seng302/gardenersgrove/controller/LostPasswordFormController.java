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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidator.isEmailValid;

/**
 * Controller for reset password  form.
 * Note the @link{Autowired} annotation giving us access to the @link{FormService} class automatically
 */

@Controller
public class LostPasswordFormController {
    Logger logger = LoggerFactory.getLogger(LostPasswordFormController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenService verificationTokenService;
    private final MailService mailService;


    @Autowired
    public LostPasswordFormController(UserService userService, AuthenticationManager authenticationManager,
                                      VerificationTokenService verificationTokenService,
                                      MailService mailService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.verificationTokenService = verificationTokenService;
        this.mailService = mailService;
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

        if (email.isEmpty() || !isEmailValid(email)) {
            model.addAttribute("lostPasswordEmailError", "Email address must be in the form ‘jane@doe.nz’");
        }
        if (model.containsAttribute("lostPasswordEmailError")) {
            return "lostPasswordFormTemplate";
        } else {
            // Email is valid

            model.addAttribute("confirmationMessage", "An email was sent to the address if it was recognised. \n\nYou can close this tab now.");

            if (userService.emailExists(email)) {
                logger.info("Email is registered in system: " + email);
                // Create Verification Token
                User newUser = userService.getUserByEmail(email);
                newUser.grantAuthority("ROLE_UNVERIFIED");
                VerificationToken verificationToken = verificationTokenService.createVerificationToken(newUser);

                // Create confirmation email
                String emailSubject = "Reset Your Password for Gardener's Grove";

                String emailText = generateResetPasswordEmail(verificationToken, newUser);

                model.addAttribute("emailText", emailText); // For testing purposes :)


                // Try to send confirmation email
                try {
                    // Send confirmation email
                    mailService.sendSimpleMessage(email, emailSubject, emailText);

                } catch (Exception e) {
                    // Log the error
                    logger.error("Failed to send confirmation code to " + email, e);
                    // no feedback to user right now
                }
            }
            return "lostPasswordFormTemplate"; // todo implement popup confirmation message modal using Boostrap spike
        }
    }

    public static String generateResetPasswordEmail(VerificationToken verificationToken, User newUser) {
        String tokenLink = "http://localhost:8080/reset-password-form?token=" + verificationToken.getToken();

        String emailText = "Dear " + newUser.getFirstName() + ",\n\n" +
                "To reset your password, please use the following link:\n\n" +
                tokenLink +
                "\n\nIf you did not request this code or have any questions, please contact our support team.\n\n" +
                "Thank you for using Gardener's Grove! Happy gardening!";
        return emailText;
    }
}
