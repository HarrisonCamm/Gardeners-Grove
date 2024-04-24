package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public class AddPlantPictureController {

    private final PlantService plantService;

    public AddPlantPictureController(PlantService plantService) {
        this.plantService = plantService;
    }

    @PostMapping("/add-plant-picture")
    public String addPlantPicture(@RequestParam("file") MultipartFile file, @RequestParam("plantId") Long plantId) {

        // Write the picture to file system
        Plant plant = plantService.findPlant(plantId).get();
        plant.setPicture(String.valueOf(file));
        return "viewGardenDetailsTemplate";
    }
}
