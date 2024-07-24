package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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



    @GetMapping("/tag")
    public String moderateTags(Model model) {
        logger.info("GET /tag");
        return "tagTemplate";
    }

    @PostMapping("/tag")
    public String moderateTagsPost(@RequestParam String tag, Model model) {
        logger.info("POST /tag " + tag);

        if (!tag.isBlank()) {
            String possibleTerms = moderationService.moderateText(tag);

            logger.info(possibleTerms + " returned terms in tag mod");

            if (!possibleTerms.equals("null")) {
                logger.info("possible terms not = null");
                model.addAttribute("tagError", "Profanity or inappropriate language detected");
            } else {
                logger.info("valid terms");
                model.addAttribute("tagError", "valid terms");
            }
        }

        return "tagTemplate";
    }

}
