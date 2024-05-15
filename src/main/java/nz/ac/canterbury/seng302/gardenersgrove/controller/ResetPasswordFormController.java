package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
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

import java.util.Timer;
import java.util.TimerTask;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidator.isPasswordValid;

@Controller
public class ResetPasswordFormController {
    Logger logger = LoggerFactory.getLogger(ResetPasswordFormController.class);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenService verificationTokenService;
    private final MailService mailService;

    private static final String PASSWORD_ERROR = "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.";

    @Autowired
    public ResetPasswordFormController(UserService userService, AuthenticationManager authenticationManager,
                                      VerificationTokenService verificationTokenService, MailService mailService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.verificationTokenService = verificationTokenService;
        this.mailService = mailService;
    }

    /**
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf resetPasswordFormTemplate
     */
    @GetMapping("/reset-password-form")
    public String form(@RequestParam(name = "token", required = true) String token,
                       Model model) {
        logger.info("GET /reset-password-form");
        model.addAttribute("token", token);
        model.addAttribute("newPassword", "");
        model.addAttribute("retypedPassword", "");
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!verificationTokenService.findAllTokens().isEmpty()) {
                    verificationTokenService.cleanupExpiredTokens();
                }
            }
        }, 0, 10000);

        // If token is expired or null
        if (!verificationTokenService.validateToken(token)) {
            logger.info("Reset password link with token " + token + " has expired.");
            return "redirect:/sign-in-form?token=" + token; //todo show error message in sign in form no matter how many times link is clicked
        }

        return "resetPasswordFormTemplate";
    }

    /**
     * Posts a form response with email
     * @param newPassword newPassword set by user
     * @param retypedPassword user retyped password
     * @param token the token in the URL from the email
     * @param model (map-like) representation for use in thymeleaf,
     *              with values being set to relevant parameters provided
     * @return thymeleaf lostPasswordFormTemplate and popup message
     */
    @PostMapping("/reset-password-form")
    public String submitForm(@RequestParam(name="newPassword") String newPassword,
                             @RequestParam(name = "retypedPassword") String retypedPassword,
                             @RequestParam(name = "token", required = true) String token,
                             HttpServletRequest request,
                             Model model) {
        logger.info("POST /reset-password-form");

        // Getting the user for sending the confirmation email and validating retyped password
        // To check if the user's fields match the password or not AC7
        User currentUser = verificationTokenService.getUserByToken(token);


        model.addAttribute("newPassword", newPassword);
        model.addAttribute("retypePassword", retypedPassword);
        model.addAttribute("token", token);

        // Check if the new password is empty
        if (newPassword == null || newPassword.isEmpty() || newPassword.length() > 512){
            model.addAttribute("newPasswordError", PASSWORD_ERROR);
        } else {
            // Validate the new password strength
            if (!isPasswordValid(newPassword)) {
                model.addAttribute("newPasswordError", PASSWORD_ERROR);
            } else if (newPassword.toLowerCase().contains(currentUser.getEmail().toLowerCase()) ||
                    newPassword.toLowerCase().contains(currentUser.getFirstName().toLowerCase()) ||
                    (!currentUser.getLastName().isEmpty() && newPassword.toLowerCase().contains(currentUser.getLastName().toLowerCase())) ||
                    (!currentUser.getDateOfBirth().isEmpty() && newPassword.contains(currentUser.getDateOfBirth()))) {
                model.addAttribute("newPasswordError", PASSWORD_ERROR);
            }
        }

        // Check if the retyped password is empty
        if (retypedPassword == null || retypedPassword.isEmpty()) {
            model.addAttribute("passwordMatchError", "Retyping the new password is required.");
        } else {
            // Check if the new password and retype password match
            if (!newPassword.equals(retypedPassword)) {
                model.addAttribute("passwordMatchError", "The new passwords do not match");
            }
        }

        if (model.containsAttribute("newPasswordError") || model.containsAttribute("passwordMatchError")) {
            return "resetPasswordFormTemplate";
        } else {
            // new password and retyped password are valid
            logger.info("Password is valid, user has token " + token );
            logger.info("User first name is: " + currentUser.getFirstName());

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
                logger.info("Sent confirmation email to " + emailAddress);

                // Password updated, allow user to login page
                // todo check authentication of user??

                return "redirect:/sign-in-form?token=" + token;
            } catch (Exception e) {
                // Log the error
                logger.error("Failed to send password change confirmation email to " + emailAddress, e);
                // TODO display an error message

            }
        }
        return "resetPasswordFormTemplate"; //technically this line should never run(?)
    }

}
