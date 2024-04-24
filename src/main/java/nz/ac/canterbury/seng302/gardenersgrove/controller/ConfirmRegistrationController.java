package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.TemporaryUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.TemporaryUserService;
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
import java.util.Objects;

/**
 * Controller for registration form.
 * Note the @link{Autowired} annotation giving us access to the @link{FormService} class automatically
 */
@Controller
public class ConfirmRegistrationController {
    Logger logger = LoggerFactory.getLogger(ConfirmRegistrationController.class);

    private final UserService userService;
    private final TemporaryUserService temporaryUserService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public ConfirmRegistrationController(UserService userService,
                                         TemporaryUserService temporaryUserService,
                                         AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.temporaryUserService = temporaryUserService;
        this.authenticationManager = authenticationManager;
    }
    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf demoFormTemplate
     */
    @GetMapping("/confirm-registration")
    public String form(Model model) {
        logger.info("GET /confirm-registration");
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
        TemporaryUser tempUser = temporaryUserService.getUserById(1L);
        int code = tempUser.getCode();

        if (Objects.equals(registrationCode, String.valueOf(code))) {

            String firstName = tempUser.getFirstName();
            String lastName = tempUser.getLastName();
            boolean noLastName = tempUser.getNoLastName();
            String email = tempUser.getEmail();
            String password = tempUser.getPassword();
            String dateOfBirth = tempUser.getDateOfBirth();

            temporaryUserService.deleteTemporaryUserById(1L);
            User newUser = new User(firstName, lastName, noLastName, email, password, dateOfBirth);

            // Grant user role
            newUser.grantAuthority("ROLE_USER");

            // Register user
            userService.saveUser(newUser);

            // Auto-login security stuff
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            // Set the authenticated user in the session
            request.getSession().setAttribute("user", newUser);

            return "redirect:/sign-in-form";
        } else {
            model.addAttribute("signupCodeError", "Signup code invalid");
            return "confirmRegistrationTemplate";
        }

    }
}
