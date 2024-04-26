package nz.ac.canterbury.seng302.gardenersgrove.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /**
     * Gets the thymeleaf page representing the /sign-in page (a basic welcome screen with some links)
     * @param model (map-like) representation of data to be used in thymeleaf display
     * @return thymeleaf demoTemplate
     */
    @GetMapping("/sign-in-form")
    public String getTemplate(@RequestParam(value = "error", required = false) String error,
                              Model model) {
        logger.info("GET /sign-in-form");
        if (error != null) {
            model.addAttribute("signInError", "The email address is unknown, or the password is invalid");
        }
        // Pass the email value back to the template
        model.addAttribute("email", "");
        return "signInTemplate";
    }
}
