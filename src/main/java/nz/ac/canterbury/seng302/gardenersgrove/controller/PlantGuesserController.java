package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for viewing the plant guesser page
 */

@Controller
public class PlantGuesserController {

    Logger logger = LoggerFactory.getLogger(PlantGuesserController.class);

    public PlantGuesserController() {

    }

    /**
     * Gets the thymeleaf page showing the plant guesser page
     */
    @GetMapping("/plant-guesser")
    public String getTemplate(HttpServletRequest request,
                              Model model) {
        logger.info("GET /plant-guesser");
        RedirectService.addEndpoint("/plant-guesser");

        return "plantGuesserTemplate";
    }
}
