package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantData;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantGuesserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

/**
 * Controller for viewing the plant guesser page
 */

@Controller
public class PlantGuesserController {

    Logger logger = LoggerFactory.getLogger(PlantGuesserController.class);

    private final PlantGuesserService plantGuesserService;
    private Random random;

    public PlantGuesserController(PlantGuesserService plantGuesserService, Random random) {
        this.plantGuesserService = plantGuesserService;
        this.random = random;
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

        createPlantGameRound(model);

        return "plantGuesserTemplate";
    }

    public void createPlantGameRound(Model model) {

        String plantName = null;
        String plantScientificName;
        String plantImage = null;
        String imageCredit = null;
        String plantFamily = null;
        String familyCommonName = null;
        String plantCommonAndScientificName;
        List<String> quizOptions = null;
        int listSize = 0;

        while (listSize != 4) {
            PlantData plant = plantGuesserService.getPlant();
            plantName = plant.common_name;
            plantScientificName = plant.scientific_name;
            plantImage = plant.image_url;
            imageCredit = (plantImage.split("//")[1]).split("/")[0];
            plantFamily = plant.family;
            familyCommonName = plant.family_common_name;
            plantCommonAndScientificName = plantName + ",\n(" + plantScientificName + ")";
            quizOptions = plantGuesserService.getMultichoicePlantNames(plantFamily, plantName, plantCommonAndScientificName);
            listSize = quizOptions.size();
            logger.info(plantName); //for manual testing and playing, since functionality is not implemented yet
        }

        Collections.shuffle(quizOptions, random); // set random while testing, otherwise true random

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
