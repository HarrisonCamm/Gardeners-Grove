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

import java.util.List;
import java.util.Random;

/**
 * Controller for viewing the plant guesser page
 */

@Controller
public class PlantGuesserController {

    Logger logger = LoggerFactory.getLogger(PlantGuesserController.class);
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

        PlantData plant = plantGuesserService.getPlant();
        String plantName = plant.common_name;
        String plantImage = plant.image_url;
        String plantFamily = plant.family;
        List<String> quizOptions = plantGuesserService.getMultichoicePlantNames(plantFamily, plantName);
        logger.info(String.valueOf(quizOptions));
        model.addAttribute("commonName", plantName);
        model.addAttribute("plantImage", plantImage);

        return "plantGuesserTemplate";
    }
}
