package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
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
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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

        ArrayList<FieldError> errors = checkFields(newPlant.getName(), newPlant.getDescription(), newPlant.getCount());

        String formattedDate;
        formattedDate = convertDateFormat(datePlanted);
        //Validates input fields
        checkDateValidity(formattedDate, bindingResult);

        //Parses the datePlanted string into a Date object or leave as null if date is invalid
        //Currently a dateError can never be caused
        Date date = null;
        if (datePlanted != null && !datePlanted.trim().isEmpty()) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(datePlanted);
            } catch (Exception e) {
                errors.add(new FieldError("plant", "datePlanted", "Date should be in the format dd/mm/yyyy"));
            }
        }
        //Sets assigns the new values to the original plant object ready to be saved to the database
        plant.setDatePlanted(date);
        plant.setName(newPlant.getName());
        plant.setCount(newPlant.getCount());
        plant.setDescription(newPlant.getDescription());

        model.addAttribute("lastEndpoint", RedirectService.getPreviousPage());
        model.addAttribute("plantID", plantID); // Add gardenID to the model
        //Ternary operator to assign null date or assign a formatted date
        model.addAttribute("datePlanted", (date != null) ? new SimpleDateFormat("yyyy-MM-dd").format(formattedDate) : "");


        if (!errors.isEmpty()) {
            for (FieldError error : errors) {
                model.addAttribute(error.getField().replace('.', '_') + "Error", error.getDefaultMessage());}
            model.addAttribute("plant", plant);             // I don't understand why but if I remove this line all fields are cleared if they have errors, except name for no reason
            return "editPlantFormTemplate";
        } else {
            plantService.addPlant(plant);
            return "redirect:/view-garden?gardenID=" + plant.getGarden().getId();
        }
    }

    /**
     * Checks all input strings with PlantValidator validation methods
     * And generates a list of errors
     * @param plantName A string representing a plant name
     * @param plantDescription A string representing a plant description
     * @param plantCount A string representing a plant count
     * @return An Arraylist<FieldError> object containing all
     */
    public ArrayList<FieldError> checkFields(String plantName, String plantDescription, String plantCount) {
        ArrayList<FieldError> errors = new ArrayList<>();

        FieldError nameError = validatePlantName(plantName);
        if (nameError != null) {errors.add(nameError);}

        FieldError descriptionError = validatePlantDescription(plantDescription);
        if (descriptionError != null) {errors.add(descriptionError);}

        FieldError countError = validatePlantCount(plantCount);
        if (countError != null) {errors.add(countError);}

        return errors;
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


    private void checkDateValidity(String date, BindingResult bindingResult) {
        ObjectError dateError = validatePlantDate(date);
        if (dateError != null) {
            bindingResult.addError(dateError);
        }
    }
    public static String convertDateFormat(String dateInput) {
        String[] parts = dateInput.split("/");
        if (dateInput.length() < 10) {
            return "0000-00-00";
        } else {
            // Reconstruct the date string in yyyy-MM-dd format
            String yyyy = parts[2];
            String mm = parts[1];
            String dd = parts[0];

            // Ensure mm and dd are formatted with leading zeros if necessary
            if (mm.length() == 1) {
                mm = "0" + mm;
            }
            if (dd.length() == 1) {
                dd = "0" + dd;
            }
            return yyyy + "-" + mm + "-" + dd;
        }
    }

}
