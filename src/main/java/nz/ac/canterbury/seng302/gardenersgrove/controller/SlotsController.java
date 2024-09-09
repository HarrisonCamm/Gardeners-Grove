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

    private static final int SPIN_COST = 50;
    private static final String BUTTON_TEXT = "SPIN";
    private static final String MESSAGE = "You have a free spin available!";
    private static final String BUTTON_TEXT_PAYED = "SPIN for " + SPIN_COST + "฿";
    private static final String MESSAGE_PAYED = "You've already spun today! Spend " + SPIN_COST + "฿ to spin again?";

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

        User user = userService.getAuthenticatedUser();
        model.addAttribute("bloomBalance", user.getBloomBalance());

        //Seems logical to use one function for postMapping and getMapping even though amountWon isn't used here
        int amountWon = processSlots(session, model);

        logger.info(String.valueOf(amountWon));


        GameState gameState = session.getAttribute("gameState") == null ? GameState.FREE_SPIN : (GameState) session.getAttribute("gameState");

        switch(gameState) {
            case FREE_SPIN:
                if (isWithin24Hours(user.getLastFreeSpinUsed())) {
                    session.setAttribute("gameState", GameState.PAYED_SPIN);
                    return "redirect:/daily-spin";
                } else {
                    setSessionModelAttributes(session, model,
                            GameState.FREE_SPIN, GameState.FREE_SPIN,
                            BUTTON_TEXT, MESSAGE, GameState.FREE_SPINNING);
                    break;
                }
            case PAYED_SPIN:
                setSessionModelAttributes(session, model,
                        GameState.PAYED_SPIN, GameState.PAYED_SPIN,
                        BUTTON_TEXT_PAYED, MESSAGE_PAYED, GameState.PAYED_SPINNING);
                break;
            default:
                session.setAttribute("gameState", GameState.FREE_SPIN);
                return "redirect:/daily-spin";
        }

        return "dailySpinTemplate";
    }

    @PostMapping("/daily-spin")
    public String postTemplate(@RequestParam("gameState") String buttonActionString, HttpSession session, Model model) throws InterruptedException {
        logger.info("POST /daily-spin");

        User user = userService.getAuthenticatedUser();
        model.addAttribute("bloomBalance", user.getBloomBalance());

        //This handles form resubmission:
        if (session.getAttribute("slots") == null) {
            return "redirect:/daily-spin";
        }

        int amountWon = processSlots(session, model);

        GameState buttonActionGameState;
        try {
            buttonActionGameState = GameState.valueOf(buttonActionString);
        } catch (IllegalArgumentException e) {
            logger.info("User tried to cheat");
            Thread.sleep(1000); // Punish user for trying to cheat
            return "redirect:/daily-spin";
        }

        switch (buttonActionGameState) {
            case FREE_SPINNING:
                if (isWithin24Hours(user.getLastFreeSpinUsed())) {
                    session.setAttribute("gameState", GameState.PAYED_SPIN);
                    return "redirect:/daily-spin";
                } else {
                    freeSpin(user, amountWon);
                    setSessionModelAttributes(session, model,
                            GameState.FREE_SPIN, GameState.FREE_SPINNING,
                            BUTTON_TEXT, MESSAGE, GameState.FREE_SPINNING);

                    model.addAttribute("spinCost", 0);

                    session.setAttribute("slots", null); //Reset slots
                }
                break;
            case PAYED_SPINNING:
                payedSpin(user, amountWon);
                setSessionModelAttributes(session, model,
                        GameState.PAYED_SPIN, GameState.PAYED_SPINNING,
                        BUTTON_TEXT_PAYED, MESSAGE_PAYED, GameState.PAYED_SPINNING);

                model.addAttribute("spinCost", SPIN_COST);

                session.setAttribute("slots", null); //Reset slots
                break;
            default:
                session.setAttribute("gameState", GameState.FREE_SPIN);
                return "redirect:/daily-spin";
        }

        return "dailySpinTemplate";
    }

    private void setSessionModelAttributes(HttpSession session, Model model,
                                           GameState sessionGameState, GameState gameState,
                                           String buttonText, String message, GameState buttonAction) {
        session.setAttribute("gameState", sessionGameState);

        model.addAttribute("gameState", gameState);
        model.addAttribute("buttonText", buttonText);
        model.addAttribute("message", message);
        model.addAttribute("buttonAction", buttonAction);
    }

    private void freeSpin(User user, int amountWon) {
        userService.addBlooms(user, amountWon);
        user.UpdateLastFreeSpinUsed();
        userService.updateUserFriends(user);        //Only update method that just takes user as a parameter
    }

    private void payedSpin(User user, int amountWon) {
        userService.addBlooms(user, amountWon);
        userService.chargeBlooms(user, SPIN_COST);
    }

    private boolean isWithin24Hours(Date date) {
        if (date == null) return false;
        else return Duration.between(date.toInstant(), Instant.now()).toHours() < 24;
    }

    /**
     * Generates or retrieves slots from session and adds them to the model
     * @param session The session to get the slots from
     * @param model The model to add the slots to
     * @return The amount won by the player
     */
    private int processSlots(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<int[]> slots = (List<int[]>) session.getAttribute("slots");

        //If there is not a set of slots generates new ones
        if (slots == null) {
            slots = SlotsService.generateSlots();
            session.setAttribute("slots", slots);
        }

        model.addAttribute("slots", slots);
        int amountWon = SlotsService.amountWon(slots);
        model.addAttribute("amountWon", amountWon);
        return amountWon;
    }
}
