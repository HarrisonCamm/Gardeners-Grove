package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.service.PlantGuesserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GamesController {
    Logger logger = LoggerFactory.getLogger(GamesController.class);
    private final PlantGuesserService plantGuesserService;

    public GamesController(PlantGuesserService plantGuesserService) {
        this.plantGuesserService = plantGuesserService;
    }

    @GetMapping("/games")
    public String gamesPage(HttpSession session,
                            Model model) {
        logger.info("GET /games");
        RedirectService.addEndpoint("/games");
        plantGuesserService.excludePlantFamilies();
        model.addAttribute("guesserGameError", session.getAttribute("guesserGameErrorMessage"));
        session.removeAttribute("guesserGameErrorMessage");
        return "gamesTemplate";
    }

}
