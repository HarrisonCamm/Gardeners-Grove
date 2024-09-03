package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantData;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantGuesserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/**
 * Controller for viewing the plant guesser page
 */

@Controller
public class PlantGuesserController {

    Logger logger = LoggerFactory.getLogger(PlantGuesserController.class);

    @Autowired
    private final PlantGuesserService plantGuesserService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final UserRepository userRepository;
    private List<PlantData> plants;
    private int roundNumber = 0;
    private int score = 0;

    public PlantGuesserController(PlantGuesserService plantGuesserService, UserService userService, UserRepository userRepository) {
        this.plantGuesserService = plantGuesserService;
        this.plants = plantGuesserService.getPlant();
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Gets the thymeleaf page showing the plant guesser page
     */
    @GetMapping("/plant-guesser")
    public String getTemplate(HttpServletRequest request,
                              Model model) {
        logger.info("GET /plant-guesser");
        RedirectService.addEndpoint("/plant-guesser");

        playGameRound(model, plants.get(roundNumber));
        return "plantGuesserTemplate";
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
                               HttpServletRequest request,
                              Model model) {
        logger.info("POST /plant-guesser");
        RedirectService.addEndpoint("/plant-guesser");
        User currentUser = userService.getAuthenticatedUser();
        int currentBloomBalance = currentUser.getBloomBalance();

        String[] quizOptions = {quizOption1, quizOption2, quizOption3, quizOption4};
        List<String[]> splitQuizOptions = new ArrayList<>();
        for (String option: quizOptions) {
            String[] options = option.split(",");
            splitQuizOptions.add(options);
        }

        model.addAttribute("plantFamily", plantFamily);
        model.addAttribute("quizOptions", splitQuizOptions);
        model.addAttribute("plantImage", plantImage);
        model.addAttribute("imageCredit", imageCredit);
        model.addAttribute("roundNumber", roundNumber);
        model.addAttribute("correctOption", correctOption);
        model.addAttribute("selectedOption", selectedOption);

        if (selectedOption != correctOption) {
            model.addAttribute("incorrectAnswer", "Wrong answer! The correct answer was: " + splitQuizOptions.get(correctOption)[0]);
        } else {
            this.score += 1;
            model.addAttribute("correctAnswer", "You got it correct! +10 Blooms");
        }
        this.roundNumber += 1;
        model.addAttribute("score", this.score);

        if (roundNumber < 10) {
            model.addAttribute("answerSubmitted", true);
            model.addAttribute("gameOver", false);
        } else {
            model.addAttribute("answerSubmitted", false);
            model.addAttribute("gameOver", true);
            currentUser.setBloomBalance(currentBloomBalance + 100 + (this.score*10));
            logger.info(String.valueOf(currentUser.getBloomBalance()));
            userRepository.save(currentUser);
        }

        return "plantGuesserTemplate";
    }

    public void playGameRound(Model model, PlantData plant) {
        String plantName = plant.common_name;
        String plantScientificName = plant.scientific_name;
        String plantImage = plant.image_url;
        String imageCredit = (plantImage.split("//")[1]).split("/")[0];
        String plantFamily = plant.family;
        String familyCommonName = plant.family_common_name;
        String plantCommonAndScientificName = plantName + ",\n(" + plantScientificName + ")";
        List<String> quizOptions = plantGuesserService.getMultichoicePlantNames(plantFamily, plantName, plantCommonAndScientificName);

        Collections.shuffle(quizOptions);

        List<String[]> splitQuizOptions = new ArrayList<>();
        for (String option: quizOptions) {
            String[] options = option.split(",");
            splitQuizOptions.add(options);
        }
        int correctOption = 0;
        for (int i = 0; i < 4; i++) {
            if (splitQuizOptions.get(i)[1].contains(plant.scientific_name)) {
                correctOption = i;
            }
        }

        model.addAttribute("plantFamily", familyCommonName == null ? plantFamily : familyCommonName);
        model.addAttribute("quizOptions", splitQuizOptions);
        model.addAttribute("plantImage", plantImage);
        model.addAttribute("imageCredit", imageCredit + " via Trefle");
        model.addAttribute("roundNumber", this.roundNumber + 1);
        model.addAttribute("correctOption", correctOption);
        model.addAttribute("selectedOption", -1);
        model.addAttribute("answerSubmitted", false);
        model.addAttribute("gameOver", false);
        model.addAttribute("score", this.score);

    }
}
