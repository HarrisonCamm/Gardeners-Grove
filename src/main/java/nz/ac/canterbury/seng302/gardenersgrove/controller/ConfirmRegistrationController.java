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
public class ConfirmRegistrationController {
    Logger logger = LoggerFactory.getLogger(ConfirmRegistrationController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public ConfirmRegistrationController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }
    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     * @param displayName previous name entered into form to be displayed
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf demoFormTemplate
     */
    @GetMapping("/confirm-registration")
    public String form(@RequestParam(name="displayName", required = false, defaultValue = "") String displayName,
                       Model model) {
        logger.info("GET /confirm-registration");
        model.addAttribute("displayName", displayName);
        return "confirmRegistrationTemplate";
    }
    /**
     * Posts a form response with name and favourite language
     * @param registrationCode confirmation code sent to email
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf,
     *              with values being set to relevant parameters provided
     * @return thymeleaf demoFormTemplate
     */
    @PostMapping("/confirm-registration")
    public String submitForm(@RequestParam(name="registrationCode") String registrationCode,
                             HttpServletRequest request,
                             Model model) {
        logger.info("POST /confirm-registration");
        model.addAttribute("registrationCode", registrationCode);

        if (Objects.equals(registrationCode, "123")) {
            return "redirect:/view-user-profile";
        } else {
            return "confirmRegistrationTemplate";
        }

    }
}
