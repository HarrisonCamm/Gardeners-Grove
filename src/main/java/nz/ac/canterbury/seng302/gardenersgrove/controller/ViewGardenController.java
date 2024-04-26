package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ViewGardenController {

    Logger logger = LoggerFactory.getLogger(ViewGardenController.class);

    private final GardenService gardenService;
    private final PlantService plantService;

    public ViewGardenController(GardenService gardenService, PlantService plantService) {
        this.gardenService = gardenService;
        this.plantService  = plantService;
    }

    @GetMapping("/view-garden")
    public String viewGarden(@RequestParam("gardenID") Long gardenID, Model model, HttpServletResponse response) {
        // Add cache control headers
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        logger.info("GET /view-garden");
        RedirectService.addEndpoint("/view-garden?gardenID=" + gardenID);

        return addAttributes(gardenID, model, plantService, gardenService);
    }

    @PostMapping("/view-garden")
    public String addPlantPicture(@RequestParam("plantID") Long plantID,
                                  @RequestParam("gardenID") Long gardenID,
                                  @RequestParam("file") MultipartFile file,
                                  @RequestParam(value = "continue",required = false) String continueString,
                                  Model model) {

        logger.info("POST /view-garden");

        // Write the picture to file system
        Plant plant = plantService.findPlant(plantID).get();

        plant.setPicture(file.getOriginalFilename()); // Set the new image

        Path path = Paths.get("src/main/resources/static/images/" + file.getOriginalFilename());

        plantService.addPlant(plant);

        // Write the file to the file system
        try {
            Files.createDirectories(path.getParent());
            file.transferTo(path);
        } catch (Exception e) {
            logger.error("Failed to write file to file system", e);
        }

        // Add the attributes to the model
        addAttributes(gardenID, model, plantService, gardenService);
        return "redirect:/view-garden?gardenID=" +gardenID;
    }

    static String addAttributes(@RequestParam("gardenID") Long gardenID, Model model, PlantService plantService, GardenService gardenService) {
        List<Plant> plants = new ArrayList<>();
        for (var plant : plantService.getPlants()) {
            if (plant.getGarden().getId().equals(gardenID)) {
                plants.add(plant);
            }
        }
        model.addAttribute("gardens", gardenService.getGardens());
        model.addAttribute("plants", plants);

        Optional<Garden> garden = gardenService.findGarden(gardenID);
        if (garden.isPresent()) { // if the garden ID exists
            model.addAttribute("gardenID", gardenID);
            model.addAttribute("gardenName", garden.get().getName());
            model.addAttribute("gardenLocation", garden.get().getLocation().getStreetAddress());
            model.addAttribute("gardenSize", garden.get().getSize());
            return "viewGardenDetailsTemplate";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " does not exist");
        }
    }
}
