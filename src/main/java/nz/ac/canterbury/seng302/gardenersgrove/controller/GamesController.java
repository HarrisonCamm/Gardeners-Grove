package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GamesController {

    @GetMapping("/games")
    public String gamesPage(HttpSession session,
                            Model model) {
        model.addAttribute("guesserGameError", session.getAttribute("guesserGameErrorMessage"));
        session.removeAttribute("guesserGameErrorMessage");
        return "gamesTemplate";
    }

}
