package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantData;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantGuesserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final PlantGuesserService plantGuesserService;

    public PlantGuesserController(PlantGuesserService plantGuesserService) {
        this.plantGuesserService = plantGuesserService;
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
        List<PlantData> plants = plantGuesserService.getPlant();
        logger.info(plants.get(0).common_name);
        logger.info(plants.get(0).family);
        logger.info(plants.get(1).common_name);
        logger.info(plants.get(1).family);
        logger.info(plants.get(2).common_name);
        logger.info(plants.get(2).family);
        logger.info(plants.get(3).common_name);
        logger.info(plants.get(3).family);
        logger.info(plants.get(4).common_name);
        logger.info(plants.get(4).family);
        logger.info(plants.get(5).common_name);
        logger.info(plants.get(5).family);
        logger.info(plants.get(6).common_name);
        logger.info(plants.get(6).family);
        logger.info(plants.get(7).common_name);
        logger.info(plants.get(7).family);
        logger.info(plants.get(8).common_name);
        logger.info(plants.get(8).family);
        logger.info(plants.get(9).common_name);
        logger.info(plants.get(9).family);


        playGameRound(model, plants.get(0));
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
        logger.info(plantName); //for manual testing and playing, since functionality is not implemented yet
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
        model.addAttribute("roundNumber", 1);
        model.addAttribute("correctOption", correctOption);

//        TODO change this number to be for each round, not to be done in this task
    }
}
