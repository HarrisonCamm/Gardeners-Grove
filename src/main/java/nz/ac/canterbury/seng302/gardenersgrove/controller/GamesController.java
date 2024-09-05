package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GamesController {
    Logger logger = LoggerFactory.getLogger(GamesController.class);

    @GetMapping("/games")
    public String gamesPage(Model model) {
        logger.info("GET /games");
        RedirectService.addEndpoint("/games");
        return "gamesTemplate";
    }

}
