package nz.ac.canterbury.seng302.gardenersgrove.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GamesController {

    @GetMapping("/games")
    public String gamesPage(Model model) {
        return "gamesTemplate";
    }

}
