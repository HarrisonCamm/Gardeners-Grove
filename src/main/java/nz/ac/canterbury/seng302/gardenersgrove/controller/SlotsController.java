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
    private static final String REDIRECT_STR = "redirect:/daily-spin";

    private static final String GAME_STATE_ATTRIBUTE = "gameState";
    private static final String SLOTS_ATTRIBUTE = "slots";


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

        processSlots(session, model);

        GameState gameState = session.getAttribute(GAME_STATE_ATTRIBUTE) == null ? GameState.FREE_SPIN : (GameState) session.getAttribute(GAME_STATE_ATTRIBUTE);

        switch(gameState) {
            case FREE_SPIN:
                if (isWithin24Hours(user.getLastFreeSpinUsed())) {
                    session.setAttribute(GAME_STATE_ATTRIBUTE, GameState.PAYED_SPIN);
                    return REDIRECT_STR;
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
                session.setAttribute(GAME_STATE_ATTRIBUTE, GameState.FREE_SPIN);
                return REDIRECT_STR;
        }

        return "dailySpinTemplate";
    }

    @PostMapping("/daily-spin")
    public String postTemplate(@RequestParam("buttonAction") String buttonActionString, HttpSession session, Model model) throws InterruptedException {
        logger.info("POST /daily-spin");

        User user = userService.getAuthenticatedUser();
        model.addAttribute("bloomBalance", user.getBloomBalance());

        //This handles form resubmission:
        if (session.getAttribute(SLOTS_ATTRIBUTE) == null) {
            return REDIRECT_STR;
        }

        int amountWon = processSlots(session, model);

        GameState buttonActionGameState;
        try {
            buttonActionGameState = GameState.valueOf(buttonActionString);
        } catch (IllegalArgumentException e) {
            logger.info("User tried to cheat");
            Thread.sleep(1000); // Punish user for trying to cheat
            return REDIRECT_STR;
        }

        switch (buttonActionGameState) {
            case FREE_SPINNING:
                if (isWithin24Hours(user.getLastFreeSpinUsed())) {
                    session.setAttribute(GAME_STATE_ATTRIBUTE, GameState.PAYED_SPIN);
                    return REDIRECT_STR;
                } else {
                    freeSpin(user, amountWon);
                    setSessionModelAttributes(session, model,
                            GameState.FREE_SPIN, GameState.FREE_SPINNING,
                            BUTTON_TEXT, MESSAGE, GameState.FREE_SPINNING);

                    model.addAttribute("spinCost", 0);

                    session.setAttribute(SLOTS_ATTRIBUTE, null); //Reset slots
                }
                break;
            case PAYED_SPINNING:
                payedSpin(user, amountWon);
                setSessionModelAttributes(session, model,
                        GameState.PAYED_SPIN, GameState.PAYED_SPINNING,
                        BUTTON_TEXT_PAYED, MESSAGE_PAYED, GameState.PAYED_SPINNING);

                model.addAttribute("spinCost", SPIN_COST);

                session.setAttribute(SLOTS_ATTRIBUTE, null); //Reset slots
                break;
            default:
                session.setAttribute(GAME_STATE_ATTRIBUTE, GameState.FREE_SPIN);
                return REDIRECT_STR;
        }

        return "dailySpinTemplate";
    }

    private void setSessionModelAttributes(HttpSession session, Model model,
                                           GameState sessionGameState, GameState gameState,
                                           String buttonText, String message, GameState buttonAction) {
        session.setAttribute(GAME_STATE_ATTRIBUTE, sessionGameState);

        model.addAttribute(GAME_STATE_ATTRIBUTE, gameState);
        model.addAttribute("buttonText", buttonText);
        model.addAttribute("message", message);
        model.addAttribute("buttonAction", buttonAction);
    }

    private void freeSpin(User user, int amountWon) {
        userService.addBlooms(user, amountWon);
        user.updateLastFreeSpinUsed();
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
        List<int[]> slots = (List<int[]>) session.getAttribute(SLOTS_ATTRIBUTE);

        //If there is not a set of slots generates new ones
        if (slots == null) {
            slots = SlotsService.generateSlots();
            session.setAttribute(SLOTS_ATTRIBUTE, slots);
        }

        model.addAttribute(SLOTS_ATTRIBUTE, slots);
        int amountWon = SlotsService.amountWon(slots);
        model.addAttribute("amountWon", amountWon);
        return amountWon;
    }
}
