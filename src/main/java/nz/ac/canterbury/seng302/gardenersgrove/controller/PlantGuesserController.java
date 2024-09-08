package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantData;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantGuesserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    private Random random;
    private static final int NUM_OPTIONS = 4;
    private static final int MAX_TRIES = 5;

    public PlantGuesserController(PlantGuesserService plantGuesserService, Random random) {
        this.plantGuesserService = plantGuesserService;
        this.random = random;
    }
    public void setRandom(Random random) {
        // This is used for testing purposes, so the shuffling of the answers in not random and can stay consistent for testing
        // This setter is so it can be set to a fixed random during testing, otherwise it is always truly random
        this.random = random;
    }

    /**
     * Gets the thymeleaf page showing the plant guesser page
     */
    @GetMapping("/plant-guesser")
    public String getTemplate(HttpSession session,
                              HttpServletRequest request,
                              Model model) {
        logger.info("GET /plant-guesser");
        RedirectService.addEndpoint("/plant-guesser");

        try {
            session.removeAttribute("guesserGameError");
            createPlantGameRound(model);
            return "plantGuesserTemplate";
        } catch (Exception e){
            // if there is an error in creating the game, the app will redirect back to the games page and display an error message
            session.setAttribute("guesserGameErrorMessage", "Plant guesser could not be played right now, please try again later.");
            return "redirect:/games";
        }

    }

    public void createPlantGameRound(Model model) {

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

        while (listSize != NUM_OPTIONS && attempt < MAX_TRIES) {
            PlantData plant = plantGuesserService.getPlant();
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
            logger.info(plantName); //for manual testing and playing, since functionality is not implemented yet
            attempt++;
        }

        // to throw list size error in get mapping, since otherwise the error won't be caught until the thymeleaf parsing
        if (quizOptions.size() != NUM_OPTIONS || plantName==null || plantScientificName==null
                || imageCredit==null || plantFamily==null ) {
            throw new IllegalStateException();
        }

        Collections.shuffle(quizOptions, random); // set random while testing, otherwise true random

        // The quiz options list has a string that contains both the common and scientific name of a plant so they can be shuffled together, then they need to be split up to display the scientific name on a new line
        List<String[]> splitQuizOptions = new ArrayList<>();
        for (String option : quizOptions) {
            String[] options = option.split(",");
            splitQuizOptions.add(options);
        }
        model.addAttribute("plantFamily", familyCommonName == null ? plantFamily : familyCommonName);
        model.addAttribute("quizOptions", splitQuizOptions);
        model.addAttribute("plantImage", plantImage);
        model.addAttribute("imageCredit", imageCredit + " via Trefle");
        model.addAttribute("roundNumber", 1);
//        TODO change this number to be for each round, not to be done in this task

    }
}
