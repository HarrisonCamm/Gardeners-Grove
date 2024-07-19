package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.service.MailService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TagModerationController {

    Logger logger = LoggerFactory.getLogger(TagModerationController.class);

    @Autowired
    private final ModerationService moderationService;


    @Autowired
    public TagModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }



    @GetMapping("/tag-test")
    public String moderateTags(@RequestParam String tagText, Model model) {
        logger.info("GET /tag-test");
        return "lostPasswordFormTemplate";
    }

    @PostMapping("/tag-test")
    public String moderateTagsPost(@RequestParam String tagText, Model model) {
        logger.info("POST /tag-test");
        return "lostPasswordFormTemplate";
    }



}
