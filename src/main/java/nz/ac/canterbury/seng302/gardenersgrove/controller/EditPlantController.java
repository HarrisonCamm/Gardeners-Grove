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

        //Attempt to retrieve plant or throw ResponseStatusException
        Plant plant = retrievePlant(plantID, plantService);

        //Converts the datePlanted into a string if it is not null from plant object
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

        //Attempt to retrieve plant or throw ResponseStatusException
        Plant plant = retrievePlant(plantID, plantService);

        //Validates input fields
        checkName(newPlant.getName(), bindingResult);
        checkDescription(newPlant.getDescription(), bindingResult);
        checkCount(newPlant.getCount(), bindingResult);

        //Parses the datePlanted string into a Date object or leave as null if date is invalid
        //Currently a dateError can never be caused
        Date date = null;
        if (datePlanted != null && !datePlanted.trim().isEmpty()) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(datePlanted);
            } catch (Exception e) {
                bindingResult.addError(new ObjectError(datePlanted, "Date should be in the format dd/mm/yyyy"));
            }
        }

        //Sets assigns the new values to the original plant object ready to be saved to the database
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

    /**
     * Validates a string for a plant name
     * Adds an objectError to the binding result if the name is invalid
     * @param name A string representing a plant name
     * @param bindingResult The BindingResult object errors should be bound to
     */
    private void checkName(String name, BindingResult bindingResult) {
        ObjectError nameError = validatePlantName(name);
        if (nameError != null) {
            bindingResult.addError(nameError);
        }
    }

    /**
     * Validates a string for a plant count
     * Adds an objectError to the binding result if the count is invalid
     * @param count A String representing an integer count
     * @param bindingResult The BindingResult object errors should be bound to
     */
    private void checkCount(String count, BindingResult bindingResult) {
        ObjectError countError = validatePlantCount(count);
        if (countError != null) {
            bindingResult.addError(countError);
        }
    }

    /**
     * Validates  a string for a plant description
     * Adds an object error to the binding result if the description is invalid
     * @param description   A string representing a plant description
     * @param bindingResult The BindingResult object errors should be bound to
     */
    private void checkDescription(String description, BindingResult bindingResult) {
        ObjectError descriptionError = validatePlantDescription(description);
        if (descriptionError != null) {
            bindingResult.addError(descriptionError);
        }
    }

    /**
     * Use to retrieve a plant from the database or throw a ResponseStatusException if the plant is not found
     * @param plantID Long id should be an ID of an existing plant in the database
     * @param plantService A PlantService object used to interact with the database
     * @return The retrieved Plant object
     */
    private Plant retrievePlant(Long plantID, PlantService plantService) {
        Optional<Plant> found = plantService.findPlant(plantID);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant with ID " + plantID + " not found");
        }
        Plant plant = found.get();

        return plant;
    }

}
