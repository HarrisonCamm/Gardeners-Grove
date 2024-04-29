package nz.ac.canterbury.seng302.gardenersgrove.controller;

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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResetPasswordFormController {
    Logger logger = LoggerFactory.getLogger(ResetPasswordFormController.class);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenService verificationTokenService;
    @Autowired
    public ResetPasswordFormController(UserService userService, AuthenticationManager authenticationManager,
                                      VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.verificationTokenService = verificationTokenService;
    }

    /**
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf lostPasswordFormTemplate
     */
    @GetMapping("/reset-password-form") //todo implement token in URL
    public String form(@RequestParam(name = "token", required = false) String token,
                       Model model) {
        logger.info("GET /reset-password-form");
        model.addAttribute("token", "");

        // todo validate token and redirect to login with expired message if so
        return "resetPasswordFormTemplate";
    }


}
