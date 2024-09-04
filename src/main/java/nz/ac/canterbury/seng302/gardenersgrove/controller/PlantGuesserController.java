package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantData;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantFamilyService;
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
    private final PlantFamilyService plantFamilyService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final UserRepository userRepository;
    public int roundNumber = 0;
    public int score = 0;

    public PlantGuesserController(PlantGuesserService plantGuesserService, PlantFamilyService plantFamilyService, UserService userService, UserRepository userRepository) {
        this.plantGuesserService = plantGuesserService;
        this.plantFamilyService = plantFamilyService;
        this.userService = userService;
        this.userRepository = userRepository;
    }
    public void setRandom(Random random) {
        this.random = random;
    }
    /**
     * Gets the thymeleaf page showing the plant guesser page
     */
    @GetMapping("/plant-guesser")
    public String getTemplate(HttpServletRequest request,
                              Model model) {
        logger.info("GET /plant-guesser");
        RedirectService.addEndpoint("/plant-guesser");
        PlantData plant = plantGuesserService.getPlant();
        playGameRound(model, plant);

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
            currentUser.setBloomBalance(currentBloomBalance + 100 + (this.score*10));
            userRepository.save(currentUser);
            model.addAttribute("bloomBalance", currentUser.getBloomBalance());
            model.addAttribute("answerSubmitted", false);
            model.addAttribute("gameOver", true);
            this.roundNumber = 0;
            this.score = 0;
        }
        return "plantGuesserTemplate";
    }

    public void playGameRound(Model model, PlantData plant) {
        String plantName;
        String plantScientificName;
        String plantImage = null;
        String imageCredit = null;
        String plantFamily = null;
        String familyCommonName = null;
        String plantCommonAndScientificName;
        List<String> quizOptions = null;
        int listSize = 0;

        while (listSize != 4) {
            plantName = plant.common_name;
            plantScientificName = plant.scientific_name;
            plantImage = plant.image_url;
            imageCredit = (plantImage.split("//")[1]).split("/")[0];
            plantFamily = plant.family;
            familyCommonName = plant.family_common_name;
            plantCommonAndScientificName = plantName + ",\n(" + plantScientificName + ")";
            quizOptions = plantGuesserService.getMultichoicePlantNames(plantFamily, plantName, plantCommonAndScientificName);
            listSize = quizOptions.size();
        }

        Collections.shuffle(quizOptions, random); // set random while testing, otherwise true random

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

        String retrievedCommonName = plantFamilyService.getPlantFamily(plantFamily);

        model.addAttribute("plantFamily", familyCommonName == null ? retrievedCommonName : familyCommonName);
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
