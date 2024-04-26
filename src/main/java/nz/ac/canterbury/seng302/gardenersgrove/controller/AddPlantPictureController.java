package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static nz.ac.canterbury.seng302.gardenersgrove.controller.EditPlantController.*;
import static nz.ac.canterbury.seng302.gardenersgrove.controller.ViewGardenController.addAttributes;

@Controller
public class AddPlantPictureController {

    Logger logger = LoggerFactory.getLogger(AddPlantPictureController.class);


    private final PlantService plantService;
    private final GardenService gardenService;

    @Autowired
    public AddPlantPictureController(PlantService plantService, GardenService gardenService) {
        this.gardenService = gardenService;
        this.plantService = plantService;
    }

    @PutMapping("/add-plant-picture")
    public String addPlantPicture(@RequestParam("file") MultipartFile file,
                                  @RequestParam("plantId") Long plantID,
                                  @RequestParam("gardenId") Long gardenID,
                                  Model model) {

        logger.info("POST /add-plant-picture");

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

    @PutMapping("/edit-plant-picture")
    public String addPlantPicture(@RequestParam("file") MultipartFile file,
                                  @RequestParam("plantId") Long plantID,
                                  @RequestParam("datePlanted") String datePlanted,
                                  @ModelAttribute("plant") Plant newPlant,
                                  BindingResult bindingResult,
                                  Model model) {

        logger.info("POST /edit-plant-picture");

        bindingResult = new BeanPropertyBindingResult(newPlant, "plant");

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

        //Validates input fields
        checkName(newPlant.getName(), bindingResult);
        checkDescription(newPlant.getDescription(), bindingResult);
        checkCount(newPlant.getCount(), bindingResult);

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(datePlanted);
        } catch (Exception e) {
            bindingResult.addError(new ObjectError(datePlanted, "Date is not valid"));
        }
        plant.setDatePlanted(date);
        plant.setName(newPlant.getName());
        plant.setCount(newPlant.getCount().replace(',', '.'));
        plant.setDescription(newPlant.getDescription());

        model.addAttribute("plantID", plantID); // Add gardenID to the model
        model.addAttribute("datePlanted", new SimpleDateFormat("yyyy-MM-dd").format(date));

        if (bindingResult.hasErrors()) {
            // If there are validation errors, return to the form page
            return "editPlantFormTemplate";
        } else {
            plantService.addPlant(plant);
            return "redirect:/view-garden?gardenID=" + plant.getGarden().getId();
        }
    }
}
