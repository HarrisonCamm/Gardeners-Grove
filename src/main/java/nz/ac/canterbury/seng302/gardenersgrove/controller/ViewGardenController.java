package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
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
    private final UserService userService;

    public ViewGardenController(GardenService gardenService, PlantService plantService, UserService userService) {
        this.gardenService = gardenService;
        this.plantService  = plantService;
        this.userService = userService;
    }

    @GetMapping("/view-garden")
    public String viewGarden(@RequestParam("gardenID") Long gardenID,
                             Model model) throws ResponseStatusException {
        logger.info("GET /view-garden");
        RedirectService.addEndpoint("/view-garden?gardenID=" + gardenID);

        Optional<Garden> garden = gardenService.findGarden(gardenID);
        User currentUser = userService.getAuthenicatedUser();
        if (garden.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");
        else if (!garden.get().getOwner().equals(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view this garden.");

        List<Plant> plants = plantService.getGardenPlant(gardenID);
        List<Garden> gardens = gardenService.getOwnedGardens(currentUser.getUserId());
        model.addAttribute("gardens", gardens);
        model.addAttribute("plants", plants);

        model.addAttribute("gardenID", gardenID);
        model.addAttribute("gardenName", garden.get().getName());
        model.addAttribute("gardenLocation", garden.get().getLocation().getStreetAddress());
        model.addAttribute("gardenSize", garden.get().getSize());
        return "viewGardenDetailsTemplate";
    }

}
