package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.SlotsService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TransactionService;
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

import static java.time.temporal.ChronoUnit.DAYS;

@Controller
public class SlotsController {

    enum GameState {
        FREE_SPIN, FREE_SPINNING, PAID_SPIN, PAID_SPINNING, SPINNING
    }

    private static final int SPIN_COST = 50;
    private static final String BUTTON_TEXT = "SPIN";
    private static final String MESSAGE = "You have a free spin available!";
    private static final String BUTTON_TEXT_PAID = "SPIN for " + SPIN_COST + "฿";
    private static final String MESSAGE_PAID = "You've already spun today! Spend " + SPIN_COST + "฿ to spin again?";
    private static final String MESSAGE_INSUFFICIENT_BALANCE = "Insufficient balance! You need " + SPIN_COST + "฿ to spin again";
    private static final String REDIRECT_STR = "redirect:/daily-spin";
    private static final String LOADPAGE_STR = "dailySpinTemplate";
    private static final String GAME_STATE_ATTRIBUTE = "gameState";
    private static final String SLOTS_ATTRIBUTE = "slots";

    Logger logger = LoggerFactory.getLogger(SlotsController.class);
    private final UserService userService;
    private final TransactionService transactionService;

    private final User gardenGroveUser;

    @Autowired
    public SlotsController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.gardenGroveUser = userService.getUserByEmail("gardenersgrove@email.com");
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

        //If broken user bloom balance exits the game
        if (user.getBloomBalance() == null) {return "redirect:/games";}
        model.addAttribute("bloomBalance", user.getBloomBalance());

        processSlots(session, model);

        //Retrieves the previous game state or if none exists resits to start state (FREE_SPIN)D
        GameState gameState = session.getAttribute(GAME_STATE_ATTRIBUTE) == null ? GameState.FREE_SPIN : (GameState) session.getAttribute(GAME_STATE_ATTRIBUTE);

        //Transition table to next state
        switch(gameState) {
            case FREE_SPIN:
                if (isWithin24Hours(user.getLastFreeSpinUsed())) {  //If free spin already used redirects and moves to paid spin state
                    session.setAttribute(GAME_STATE_ATTRIBUTE, GameState.PAID_SPIN);
                    return REDIRECT_STR;
                } else {
                    setSessionModelAttributes(session, model,
                            GameState.FREE_SPIN, GameState.FREE_SPIN,
                            BUTTON_TEXT, MESSAGE, GameState.FREE_SPINNING);
                    break;
                }
            case PAID_SPIN:
                setSessionModelAttributes(session, model,
                        GameState.PAID_SPIN, GameState.PAID_SPIN,
                        BUTTON_TEXT_PAID, MESSAGE_PAID, GameState.PAID_SPINNING);
                break;
            default:    //Safety resets to start state (FREE_SPIN)
                session.setAttribute(GAME_STATE_ATTRIBUTE, GameState.FREE_SPIN);
                return REDIRECT_STR;
        }

        return LOADPAGE_STR;
    }

    /**
     * Processes what should happen after the user submits the /daily-spin page by hitting the spin button
     * @param buttonActionString The new game state to transition to
     * @param session The session to get the game state from
     * @param model The model to add data to
     * @return Either the html template that plays the slot-machine.js spin animation or a redirect to reset to static get mapping state
     * @throws InterruptedException caused by thread.sleep() call
     */
    @PostMapping("/daily-spin")
    public String postTemplate(@RequestParam("buttonAction") String buttonActionString, HttpSession session, Model model) throws InterruptedException {
        logger.info("POST /daily-spin");

        User user = userService.getAuthenticatedUser();

        //If broken user bloom balance exits the game
        if (user.getBloomBalance() == null) {return "redirect:/games";}
        model.addAttribute("bloomBalance", user.getBloomBalance());

        int amountWon = processSlots(session, model);   //Generates new slots and calculate amount won from them

        GameState buttonActionGameState;

        //Attempts to convert the new game state submitted by the user to a valid game state
        try {
            buttonActionGameState = GameState.valueOf(buttonActionString);
        } catch (IllegalArgumentException e) {      //Form has been submitted with transition to a non-existent state (most likely from modifying front end)
            logger.info("User tried to cheat");
            Thread.sleep(1000); // Punish user for trying to cheat
            return REDIRECT_STR;
        }

        //This switch statement processes the transition to the new state submitted by the user from the spin button in the front end
        switch (buttonActionGameState) {
            case FREE_SPINNING:
                if (isWithin24Hours(user.getLastFreeSpinUsed())) {
                    session.setAttribute(GAME_STATE_ATTRIBUTE, GameState.PAID_SPIN);        //Updates session game state from free spin to paid spin state then returns to static state through get mapping
                    return REDIRECT_STR;
                } else {
                    //This path through the switch is for completing a free spin
                    freeSpin(user, amountWon);
                    setSessionModelAttributes(session, model,
                            GameState.FREE_SPIN, GameState.FREE_SPINNING,
                            BUTTON_TEXT, MESSAGE, GameState.FREE_SPINNING);

                    model.addAttribute("spinCost", 0);  //Cost is set to 0 for free spin

                    session.setAttribute(SLOTS_ATTRIBUTE, null); //Reset slots
                }
                break;

            case PAID_SPINNING:
                //Attempts to charge user if insufficient balance returns to PAID_SPIN state with warning message
                if (!paidSpin(user, amountWon)) {
                    setSessionModelAttributes(session, model,
                            GameState.PAID_SPIN, GameState.PAID_SPIN,
                            BUTTON_TEXT_PAID, MESSAGE_INSUFFICIENT_BALANCE, GameState.PAID_SPINNING);
                    return LOADPAGE_STR;
                }

                //This path through the switch is for completing a paid spin
                setSessionModelAttributes(session, model,
                        GameState.PAID_SPIN, GameState.PAID_SPINNING,
                        BUTTON_TEXT_PAID, MESSAGE_PAID, GameState.PAID_SPINNING);

                model.addAttribute("spinCost", SPIN_COST);

                session.setAttribute(SLOTS_ATTRIBUTE, null); //Reset slots
                break;

            default:
                session.setAttribute(GAME_STATE_ATTRIBUTE, GameState.FREE_SPIN);
                return REDIRECT_STR;
        }

        return LOADPAGE_STR;
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

    /**
     * Awards the user the amount won from the free spin
     * @param user The user to award the amount to
     * @param amountWon The amount to award the user
     */
    private void freeSpin(User user, int amountWon) {
        userService.addBlooms(user, amountWon);
        userService.updateUserLastFreeSpinUsed(user);        //Only update method that just takes user as a parameter
        if (gardenGroveUser != null && amountWon > 0) transactionService.addTransaction(amountWon,"Free Daily Spin", "Game", user.getUserId(), gardenGroveUser.getUserId());
    }

    /**
     * Attempts to: charge the user for a spin and awards them the amount won
     * @param user The user to charge and award
     * @param amountWon The amount to award the user
     * @return True if the spin transaction was successful, false if the user did not have enough blooms
     */
    private boolean paidSpin(User user, int amountWon) {
        if (user.getBloomBalance() < SPIN_COST) {
            return false;
        }
        userService.addBlooms(user, amountWon);
        userService.chargeBlooms(user, SPIN_COST);

        if (gardenGroveUser != null) transactionService.addTransaction((SPIN_COST),"Payed for Daily Spin", "Game", gardenGroveUser.getUserId(), user.getUserId());
        if (gardenGroveUser != null && amountWon > 0) transactionService.addTransaction((amountWon),"Awarded for Daily Spin combo", "Game", user.getUserId(), gardenGroveUser.getUserId());
        return true;
    }

    private boolean isWithin24Hours(Date date) {
        if (date == null) return false;
        else return Duration.between(date.toInstant(), Instant.now()).toHours() < DAYS.getDuration().toHours();
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
