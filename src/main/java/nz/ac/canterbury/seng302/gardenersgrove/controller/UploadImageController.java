package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class UploadImageController {

    Logger logger = LoggerFactory.getLogger(UploadImageController.class);

    private final PlantService plantService;

    @Autowired
    public UploadImageController(PlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping("/upload-image")
    public String uploadImage(@RequestParam(value = "view-garden", required = false) boolean viewGarden,
                              @RequestParam(value = "edit-plant", required = false) boolean editPlant,
                              @RequestParam("plantID") Long plantID,
                              @RequestParam(value = "gardenID", required = false) Long gardenID,
                              Model model) {

        model.addAttribute("plant", plantService.findPlant(plantID).get());

        logger.info("GET /upload-image");

        return "uploadImageTemplate";
    }
}
