package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.TagValidator.doTagValidations;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.TagValidator.isAppropriateName;

@Controller
public class BrowseGardenController {
    Logger logger = LoggerFactory.getLogger(BrowseGardenController.class);
    private final GardenService gardenService;
    private final UserService userService;

    @Autowired
    public BrowseGardenController(GardenService gardenService, UserService userService) {
        this.gardenService = gardenService;
        this.userService = userService;
    }

    @GetMapping("/browse-gardens")
    public String browseGardens(HttpSession session,
                         Model model,
                         HttpServletResponse response) {

        logger.info("GET /browse-gardens");
        RedirectService.addEndpoint("/browse-gardens");

        List<Garden> gardens = gardenService.getPublicGardens();
        model.addAttribute("gardens", gardens);

        return "browseGardensTemplate";
    }

    @PostMapping("/browse-gardens")
    public String searchGardens(@RequestParam("search") String search,
                         HttpSession session,
                         Model model,
                         HttpServletResponse response) {

        logger.info("POST /browse-gardens");

        List<Garden> gardens = gardenService.searchPublicGardens(search);
        model.addAttribute("gardens", gardens);

        return "browseGardensTemplate";
    }

}

