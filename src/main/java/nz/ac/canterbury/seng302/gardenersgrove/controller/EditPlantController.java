package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.PlantValidator.*;

@Controller
public class EditPlantController {

    Logger logger = LoggerFactory.getLogger(EditPlantController.class);

    private final PlantService plantService;
    private final GardenService gardenService;

    @Autowired
    public EditPlantController(PlantService plantService, GardenService gardenService) {
        this.plantService = plantService;
        this.gardenService = gardenService;
    }

    @GetMapping("/edit-plant")
    public String form(@RequestParam("plantID") Long plantID,
                       Model model) {
        logger.info("GET /edit-plant");
        RedirectService.addEndpoint("/edit-plant?plantID=" + plantID);

        Optional<Plant> found = plantService.findPlant(plantID);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant with ID " + plantID + " not found");
        }
        Plant plant = found.get();

        String date = "";
        if (plant.getDatePlanted() != null) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(plant.getDatePlanted());
//            date = plant.getDatePlanted().toString();
        }

        model.addAttribute("plantID", plantID); // Add gardenID to the model
        model.addAttribute("plant", plant);
        model.addAttribute("datePlanted", date);

        return "editPlantFormTemplate";
    }


    /**
     * Submits the form when the user clicks "Create Plant" on the form.
     */
    @PutMapping("/edit-plant")
    public String submitForm(@RequestParam("plantID") Long plantID,
                             @RequestParam("datePlanted") String datePlanted,
                             @ModelAttribute("plant") Plant newPlant,
                             BindingResult bindingResult,
                             Model model) throws Exception {
        logger.info("PUT /edit-plant");

        bindingResult = new BeanPropertyBindingResult(newPlant, "plant");

        Optional<Plant> found = plantService.findPlant(plantID);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant with ID " + plantID + " not found");
        }
        Plant plant = found.get();

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

        model.addAttribute("lastEndpoint", RedirectService.getPreviousPage());
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

    /**
     * This method sets up the upload image page
     * @param plantID The ID of the plant to upload an image for
     * @param file The image file to upload
     * @return Redirect string for JavaScript
     */
    @PostMapping("edit-plant")
    public String changePicture(@RequestParam("plantID") Long plantID,
                                @RequestParam("file") MultipartFile file) {
        logger.info("POST /edit-plant");
        Optional<Plant> found = plantService.findPlant(plantID);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant with ID " + plantID + " not found");
        }
        Plant plant = found.get();

        try {
            byte[] imageBytes = file.getBytes();
            plant.setImage(imageBytes);
        } catch (IOException e) {
            logger.error("Failed to convert image to byte array", e);
        }

        plantService.addPlant(plant);

        return "redirect:/edit-plant?plantID=" + plantID;
    }

    public static void checkName(String name, BindingResult bindingResult) {
        ObjectError nameError = validatePlantName(name);
        if (nameError != null) {
            bindingResult.addError(nameError);
        }
    }

    public static void checkCount(String count, BindingResult bindingResult) {
        ObjectError countError = validatePlantCount(count);
        if (countError != null) {
            bindingResult.addError(countError);
        }
    }

    public static void checkDescription(String description, BindingResult bindingResult) {
        ObjectError descriptionError = validatePlantDescription(description);
        if (descriptionError != null) {
            bindingResult.addError(descriptionError);
        }
    }

}
