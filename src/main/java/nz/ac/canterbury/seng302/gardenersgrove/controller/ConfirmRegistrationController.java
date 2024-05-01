package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Authority;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.entity.VerificationToken;
import nz.ac.canterbury.seng302.gardenersgrove.service.AuthorityService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller for registration form.
 * Note the @link{Autowired} annotation giving us access to the @link{FormService} class automatically
 */
@Controller
public class ConfirmRegistrationController {
    Logger logger = LoggerFactory.getLogger(ConfirmRegistrationController.class);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenService verificationTokenService;
    private final AuthorityService authorityService;

    @Autowired
    public ConfirmRegistrationController(UserService userService,
                                         AuthenticationManager authenticationManager,
                                         VerificationTokenService verificationTokenService,
                                         AuthorityService authorityService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.verificationTokenService = verificationTokenService;
        this.authorityService = authorityService;
    }
    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf demoFormTemplate
     */
    @GetMapping("/confirm-registration")
    public String form(Model model) {
        logger.info("GET /confirm-registration");
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!verificationTokenService.findAllTokens().isEmpty()) {
                    clearExpiredUsers();
                }
            }
        }, 0, 5000);
        return "confirmRegistrationTemplate";
    }

    /**
     * Posts a form response with name and favourite language
     * @param registrationCode confirmation code sent to email
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf,
     *              with values being set to relevant parameters provided
     * @return thymeleaf demoFormTemplate
     */
    @Transactional
    @PostMapping("/confirm-registration")
    public String submitForm(@RequestParam(name="registrationCode") String registrationCode,
                             HttpServletRequest request,
                             Model model) {
        logger.info("POST /confirm-registration");

        model.addAttribute("registrationCode", registrationCode);

        clearExpiredUsers();


        // Check if the registration code is valid
        if (verificationTokenService.validateToken(registrationCode)) {

            // Token is valid, grab user's account
            User user = verificationTokenService.getUserByToken(registrationCode);

            authorityService.deleteByUser(user);
            // Grant user role USER
            user.grantAuthority("ROLE_USER");

            //Delete token once user has successfully confirmed registration
            verificationTokenService.deleteToken(registrationCode);

            // Auto-login security stuff
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            // Set the authenticated user in the session
            request.getSession().setAttribute("user", user);

            return "redirect:/sign-in-form";
        } else {
            model.addAttribute("signupCodeError", "Signup code invalid");
            return "confirmRegistrationTemplate";
        }

    }

    public void clearExpiredUsers() {
        verificationTokenService.cleanupExpiredTokens();

        List<Authority> authorityList = authorityService.findByRole("ROLE_UNVERIFIED");
        if (!authorityList.isEmpty()) {
            for (Authority authority: authorityList) {
                User user = authority.getUser();
                VerificationToken token = verificationTokenService.getTokenByUser(user);
                if (token == null) {
                    authorityService.deleteByUser(user);
                    userService.deleteUser(user);
                }
            }
        }
    }
}
