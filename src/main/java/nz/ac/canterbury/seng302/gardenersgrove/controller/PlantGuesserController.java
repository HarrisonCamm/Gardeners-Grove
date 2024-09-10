package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantData;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * Controller for viewing the plant guesser page
 */

@Controller
public class PlantGuesserController {

    Logger logger = LoggerFactory.getLogger(PlantGuesserController.class);

    private final PlantGuesserService plantGuesserService;
    private final PlantFamilyService plantFamilyService;

    private final TransactionService transactionService;
    private final UserService userService;
    private final UserRepository userRepository;
    private Random random;
    private static final int NUM_OPTIONS = 4;
    private static final int NUM_ROUNDS = 4;
    private static final int BLOOM_BONUS = 100;
    private static final int MAX_TRIES = 5;
    private static final String PAGE_URL = "/plant-guesser";
    private static final String SESSION_SCORE = "plantGuesserScore";
    private static final String SESSION_ROUND = "plantGuesserRound";

    private User gardenersGroveUser; //represents the sender for transactions from games

    @Autowired
    public PlantGuesserController(PlantGuesserService plantGuesserService, PlantFamilyService plantFamilyService, TransactionService transactionService, UserService userService, UserRepository userRepository, Random random) {
        this.plantGuesserService = plantGuesserService;
        this.plantFamilyService = plantFamilyService;
        this.transactionService = transactionService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.random = random;
    }
    public void setRandom(Random random) {
        // This is used for testing purposes, so the shuffling of the answers in not random and can stay consistent for testing
        // This setter is so it can be set to a fixed random during testing, otherwise it is always truly random
        this.random = random;
    }
    public void resetRound(HttpSession session) {

        session.setAttribute(SESSION_SCORE, 0);
        session.setAttribute(SESSION_ROUND, 0);
    }
    /**
     * Gets the thymeleaf page showing the plant guesser page
     */
    @GetMapping("/plant-guesser")
    public String getTemplate(HttpSession session,
                              HttpServletRequest request,
                              Model model) {
        logger.info("GET /plant-guesser");
        if (!Objects.equals(RedirectService.getPreviousPage(), PAGE_URL)) {
            resetRound(session);
        }
        RedirectService.addEndpoint(PAGE_URL);
        int roundNumber = (int) session.getAttribute(SESSION_ROUND);

        try {
            session.removeAttribute("guesserGameError");
            PlantData plant = plantGuesserService.getPlant(roundNumber);
            playGameRound(model, plant, session);
            return "plantGuesserTemplate";
        } catch (Exception e){
            // if there is an error in creating the game, the app will redirect back to the games page and display an error message
            session.setAttribute("guesserGameErrorMessage", "Plant guesser could not be played right now, please try again later.");
            return "redirect:/games";
        }

    }

    @PostMapping("/plant-guesser")
    public String postTemplate(@RequestParam("selectedOption") int selectedOption,
                               @RequestParam("plantFamily") String plantFamily,
                               @RequestParam("quizOption1") String quizOption1,
                               @RequestParam("quizOption2") String quizOption2,
                               @RequestParam("quizOption3") String quizOption3,
                               @RequestParam("quizOption4") String quizOption4,
                               @RequestParam("plantImage") String plantImage,
                               @RequestParam("imageCredit") String imageCredit,
                               @RequestParam("roundNumber") int roundNumber,
                               @RequestParam("correctOption") int correctOption,
                               @RequestParam("score") int score,
                               HttpSession session,
                               HttpServletRequest request,
                              Model model) {
        logger.info("POST /plant-guesser");
        RedirectService.addEndpoint(PAGE_URL);
        User currentUser = userService.getAuthenticatedUser();
        int currentBloomBalance = currentUser.getBloomBalance();

        String[] quizOptions = {quizOption1, quizOption2, quizOption3, quizOption4};
        List<String[]> splitQuizOptions = new ArrayList<>();
        for (String option: quizOptions) {
            if (option.contains(",")) {
                String[] options = option.split(",");
                splitQuizOptions.add(options);
            }
        }

        model.addAttribute("plantFamily", plantFamily);
        model.addAttribute("quizOptions", splitQuizOptions);
        model.addAttribute("plantImage", plantImage);
        model.addAttribute("imageCredit", imageCredit);
        model.addAttribute("roundNumber", roundNumber);
        model.addAttribute("correctOption", correctOption);
        model.addAttribute("selectedOption", selectedOption);
        model.addAttribute("bloomBonus", BLOOM_BONUS);

        if (selectedOption != correctOption) {
            model.addAttribute("incorrectAnswer", "Wrong answer! The correct answer was: " + splitQuizOptions.get(correctOption)[0]);
        } else {
            score += 1;
            session.setAttribute(SESSION_SCORE, score);
            model.addAttribute("correctAnswer", "You got it correct! +10 Blooms");

        }
        session.setAttribute(SESSION_ROUND, roundNumber);
        model.addAttribute("score", score);

        boolean answerSubmitted = false;
        boolean gameOver = false;

        if (roundNumber < NUM_ROUNDS) {
            answerSubmitted = true;
        } else {
            currentUser.setBloomBalance(currentBloomBalance + BLOOM_BONUS + (score*NUM_ROUNDS));
            userRepository.save(currentUser);
            model.addAttribute("bloomBalance", currentUser.getBloomBalance());
            Integer bloomsToAdd = BLOOM_BONUS + (score*NUM_ROUNDS);

            gardenersGroveUser = userService.getUserByEmail("gardenersgrove@email.com");
            transactionService.addTransaction(bloomsToAdd, "Plant guesser game.","type", currentUser.getUserId(), gardenersGroveUser.getUserId());
            gameOver = true;
            resetRound(session);
        }
        model.addAttribute("answerSubmitted", answerSubmitted);
        model.addAttribute("gameOver", gameOver);
        return "plantGuesserTemplate";
    }

    public void playGameRound(Model model, PlantData plant, HttpSession session) {
        String plantName = null;
        String plantScientificName = null;
        String plantImage = null;
        String imageCredit = null;
        String plantFamily = null;
        String familyCommonName = null;
        String plantCommonAndScientificName;
        List<String> quizOptions = new ArrayList<>();
        int listSize = 0;
        int attempt = 0;
        int score = (int) session.getAttribute(SESSION_SCORE);
        int roundNumber = (int) session.getAttribute(SESSION_ROUND);

        while (listSize != NUM_OPTIONS && attempt < MAX_TRIES) {
            plantName = plant.common_name;
            plantScientificName = plant.scientific_name;
            plantImage = plant.image_url;
            try {
                URI plantUri = new URI(plantImage);
                URL plantUrl = plantUri.toURL();
                imageCredit = plantUrl.getHost();
            } catch (Exception e) {
                logger.error("Invalid URL for plant image: " + plantImage, e);
            }
            plantFamily = plant.family;
            familyCommonName = plant.family_common_name;
            plantCommonAndScientificName = plantName + ",\n(" + plantScientificName + ")";
            quizOptions = plantGuesserService.getMultichoicePlantNames(plantFamily, plantName, plantCommonAndScientificName);
            listSize = quizOptions.size();
            attempt++;
        }

        // to throw list size error in get mapping, since otherwise the error won't be caught until the thymeleaf parsing
        if (quizOptions.size() != NUM_OPTIONS || plantName==null || plantScientificName==null
                || imageCredit==null || plantFamily==null ) {
            logger.info("Invalid plant error");
            throw new IllegalStateException();
        }

        Collections.shuffle(quizOptions, random); // set random while testing, otherwise true random

        // The quiz options list has a string that contains both the common and scientific name of a plant so they can be shuffled together, then they need to be split up to display the scientific name on a new line
        List<String[]> splitQuizOptions = new ArrayList<>();
        for (String option: quizOptions) {
            String[] options = option.split(",");
            splitQuizOptions.add(options);
        }

        int correctOption = 0;
        for (int i = 0; i < NUM_OPTIONS; i++) {
            if (splitQuizOptions.get(i)[1].contains(plant.scientific_name)) {
                correctOption = i;
            }
        }

        String retrievedCommonName = plantFamilyService.getPlantFamily(plantFamily);

        model.addAttribute("plantFamily", familyCommonName == null ? retrievedCommonName : familyCommonName);
        model.addAttribute("quizOptions", splitQuizOptions);
        model.addAttribute("plantImage", plantImage);
        model.addAttribute("imageCredit", imageCredit + " via Trefle");
        model.addAttribute("roundNumber", roundNumber + 1);
        model.addAttribute("correctOption", correctOption);
        model.addAttribute("selectedOption", -1);
        model.addAttribute("answerSubmitted", false);
        model.addAttribute("gameOver", false);
        model.addAttribute("score", score);

    }
}
