package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/View Garden")
    public String viewGarden(@RequestParam("gardenID") Long gardenID,
                             Model model) throws ResponseStatusException {
        logger.info("GET /View Garden");
        RedirectService.addEndpoint("/View Garden?gardenID=" + gardenID);

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
