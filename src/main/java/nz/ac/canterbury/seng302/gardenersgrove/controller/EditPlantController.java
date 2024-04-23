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
import org.springframework.web.server.ResponseStatusException;

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
        RedirectService.addEndpoint("/edit-plant?plantID=" + plantID);

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
        if (datePlanted != null && !datePlanted.trim().isEmpty()) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(datePlanted);
            } catch (Exception e) {
                bindingResult.addError(new ObjectError(datePlanted, "Date should be in the format dd/mm/yyyy"));
            }
        }
        plant.setDatePlanted(date);
        plant.setName(newPlant.getName());
        plant.setCount(newPlant.getCount());
        plant.setDescription(newPlant.getDescription());

        model.addAttribute("plantID", plantID); // Add gardenID to the model

        //Ternary operator to assign null date or assign a formatted date
        model.addAttribute("datePlanted", (date != null) ? new SimpleDateFormat("yyyy-MM-dd").format(date) : "");

        if (bindingResult.hasErrors()) {
            return "editPlantFormTemplate";
        } else {
            plantService.addPlant(plant);
            return "redirect:/view-garden?gardenID=" + plant.getGarden().getId();
        }
    }

    private void checkName(String name, BindingResult bindingResult) {
        ObjectError nameError = validatePlantName(name);
        if (nameError != null) {
            bindingResult.addError(nameError);
        }
    }

    private void checkCount(String count, BindingResult bindingResult) {
        ObjectError countError = validatePlantCount(count);
        if (countError != null) {
            bindingResult.addError(countError);
        }
    }

    private void checkDescription(String description, BindingResult bindingResult) {
        ObjectError descriptionError = validatePlantDescription(description);
        if (descriptionError != null) {
            bindingResult.addError(descriptionError);
        }
    }

}
