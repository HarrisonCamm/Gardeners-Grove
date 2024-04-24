package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class AddPlantPictureController {

    Logger logger = LoggerFactory.getLogger(AddPlantPictureController.class);


    private final PlantService plantService;

    @Autowired
    public AddPlantPictureController(PlantService plantService) {
        this.plantService = plantService;
    }

    @PostMapping("/add-plant-picture")
    public String addPlantPicture(@RequestParam("file") MultipartFile file,
                                  @RequestParam("plantId") Long plantID) {

        logger.info("POST /add-plant-picture");

        // Write the picture to file system
        Plant plant = plantService.findPlant(plantID).get();
        plant.setPicture(file.getOriginalFilename());

        Path path = Paths.get("src/main/resources/static/images/" + file.getOriginalFilename());

        // Write the file to the file system
        try {
            Files.createDirectories(path.getParent());
            file.transferTo(path);
        } catch (Exception e) {
            logger.error("Failed to write file to file system", e);
        }

        return "viewGardenDetailsTemplate";
    }
}
