package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.SlotsService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Controller
public class SlotsController {

    enum GameState {
        FREE_SPIN, FREE_SPINNING, PAYED_SPIN, PAYED_SPINNING, SPINNING
    }

    int SPIN_COST = 50;

    Logger logger = LoggerFactory.getLogger(SlotsController.class);
    private final UserService userService;

    @Autowired
    public SlotsController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets the thymeleaf page representing the /daily-spin page
     * @param model (map-like) representation of data to be used in thymeleaf display
     * @return thymeleaf demoTemplate
     */
    @GetMapping("/daily-spin")
    public String getTemplate(Model model, HttpSession session) {
        logger.info("GET /daily-spin");

        List<int[]> slots;
        User user = userService.getAuthenticatedUser();
        model.addAttribute("bloomBalance", user.getBloomBalance());


        //If there is not a set of slots generates new ones
        if (session.getAttribute("slots") == null) {
            slots = SlotsService.generateSlots();
            session.setAttribute("slots", slots);
        } else {
            slots = (List<int[]>) session.getAttribute("slots");
        }

        model.addAttribute("slots", slots);
        int amountWon = SlotsService.amountWon(slots);
        model.addAttribute("amountWon", amountWon);

        GameState gameState = session.getAttribute("gameState") == null ? GameState.FREE_SPIN : (GameState) session.getAttribute("gameState");
        logger.info("Game state: " + gameState);

        switch(gameState) {
            case SPINNING:
                session.setAttribute("gameState", GameState.PAYED_SPIN);
                model.addAttribute("buttonText", "Spin for " + SPIN_COST + " Blooms");
                model.addAttribute("message", "You've already spun today! Spend " + SPIN_COST + "฿ to spin again?");
                model.addAttribute("gameState", GameState.SPINNING);
                session.setAttribute("slots", null);
                break;
            case FREE_SPIN:
                if (isWithin24Hours(user.getLastFreeSpinUsed())) {
                    session.setAttribute("gameState", GameState.PAYED_SPIN);
                    model.addAttribute("gameState", GameState.FREE_SPINNING);
                } else {
                    session.setAttribute("gameState", GameState.FREE_SPIN);     //Todo check if this is redundant and remove
                    model.addAttribute("buttonText", "Spin");
                    model.addAttribute("message", "You have a free spin available!");
                    model.addAttribute("gameState", GameState.FREE_SPINNING);
                    break;
                }
            case PAYED_SPIN:
                session.setAttribute("gameState", GameState.PAYED_SPIN);
                model.addAttribute("buttonText", "Spin for " + SPIN_COST + " Blooms");
                model.addAttribute("message", "You've already spun today! Spend " + SPIN_COST + "B to spin again?");
                model.addAttribute("gameState", GameState.PAYED_SPINNING);
                break;
        }

        return "dailySpinTemplate";
    }

    @PostMapping("/daily-spin")
    public String postTemplate(@RequestParam("gameState") String newGameState, HttpSession session, Model model) throws InterruptedException {
        logger.info("POST /daily-spin");

        List<int[]> slots;                                                                                          //Duplicated from here:
        User user = userService.getAuthenticatedUser();
        model.addAttribute("bloomBalance", user.getBloomBalance());


        //If there is not a set of slots generates new ones
        if (session.getAttribute("slots") == null) {
            slots = SlotsService.generateSlots();
            session.setAttribute("slots", slots);
        } else {
            slots = (List<int[]>) session.getAttribute("slots");
        }

        model.addAttribute("slots", slots);
        int amountWon = SlotsService.amountWon(slots);
        model.addAttribute("amountWon", amountWon);                                                     //To here:

        GameState gameState;

        try {
            logger.info("New game state: " + newGameState);
            gameState = GameState.valueOf(newGameState);
        } catch (IllegalArgumentException e) {
            logger.info("User tried to cheat");
            Thread.sleep(1000); // Punish user for trying to cheat
            return "redirect:/daily-spin";
        }
        logger.info("Game state: " + gameState);

        switch (gameState) {
            case FREE_SPINNING:         //TODO add check here to ensure havent used free spin
                freeSpin(user, amountWon);
                session.setAttribute("gameState", GameState.SPINNING);        ///Might need to change
            case PAYED_SPINNING:
                payedSpin(user, amountWon);
                session.setAttribute("gameState", GameState.SPINNING);
        }

        return "redirect:/daily-spin";
    }

    void freeSpin(User user, int amountWon) {
        userService.addBlooms(user, amountWon);
    }

    void payedSpin(User user, int amountWon) {
        userService.addBlooms(user, amountWon);
        userService.chargeBlooms(user, SPIN_COST);
    }

    boolean isWithin24Hours(Date date) {
        if (date == null) return false;
        else return Duration.between(date.toInstant(), Instant.now()).toHours() < 24;
    }



}
