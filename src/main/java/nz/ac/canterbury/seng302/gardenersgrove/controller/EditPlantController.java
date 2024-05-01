package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;

import java.text.ParseException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Optional;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.PlantValidator.*;

@Controller
public class EditPlantController {

    Logger logger = LoggerFactory.getLogger(EditPlantController.class);

    private final PlantService plantService;
    private final GardenService gardenService;
    private final UserService userService;

    @Autowired
    public EditPlantController(PlantService plantService, GardenService gardenService, UserService userService) {
        this.plantService = plantService;
        this.gardenService = gardenService;
        this.userService = userService;
    }

    @GetMapping("/edit-plant")
    public String form(@RequestParam("plantID") Long plantID,
                       Model model) {
        logger.info("GET /edit-plant");
//        RedirectService.addEndpoint("/edit-plant?plantID=" + plantID);

        User currentUser = userService.getAuthenicatedUser();
        // Attempt to retrieve plant or throw ResponseStatusException
        Plant plant = retrievePlant(plantID, plantService);
        if (!plant.getGarden().getOwner().equals(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit this plant.")

        RedirectService.addEndpoint("/view-garden?gardenID=" + plant.getGarden().getId());

        model.addAttribute("plantID", plantID); // Add gardenID to the model
        model.addAttribute("plant", plant);
        model.addAttribute("datePlanted", plant.getDatePlanted());
        model.addAttribute("lastEndpoint", RedirectService.getPreviousPage());
        RedirectService.addEndpoint("/edit-plant?plantID=" + plantID);

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
        User currentUser = userService.getAuthenicatedUser();
        if (!plant.getGarden().getOwner().equals(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit this plant.");

        RedirectService.addEndpoint("/view-garden?gardenID=" + plant.getGarden().getId());
        String formattedDate;

        formattedDate = (datePlanted.isEmpty()) ? datePlanted : convertDateFormat(datePlanted);

        ArrayList<FieldError> errors = checkFields(newPlant.getName(), newPlant.getDescription(), newPlant.getCount(), formattedDate);

        //Sets assigns the new values to the original plant object ready to be saved to the database
        plant.setDatePlanted(datePlanted);
        plant.setName(newPlant.getName());
        plant.setCount(newPlant.getCount());
        plant.setDescription(newPlant.getDescription());

        model.addAttribute("plantID", plantID); // Add gardenID to the model
//        model.addAttribute("datePlanted", formattedDate);
        model.addAttribute("plant", plant);
        model.addAttribute("lastEndpoint", RedirectService.getPreviousPage());
        //Ternary operator to assign null date or assign a formatted date
        model.addAttribute("datePlanted", datePlanted);


        if (!errors.isEmpty()) {
            for (FieldError error : errors) {
                model.addAttribute(error.getField().replace('.', '_') + "Error", error.getDefaultMessage());}
            model.addAttribute("plant", plant);             // I don't understand why but if I remove this line all fields EXCEPT name are cleared if they have errors
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
    @PostMapping("edit-plant-picture")
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
    /**
     * Checks all input strings with PlantValidator validation methods
     * And generates a list of errors
     * @param plantName A string representing a plant name
     * @param plantDescription A string representing a plant description
     * @param plantCount A string representing a plant count
     * @return An Arraylist<FieldError> object containing all
     */
    public ArrayList<FieldError> checkFields(String plantName, String plantDescription, String plantCount, String plantDatePlanted) {
        ArrayList<FieldError> errors = new ArrayList<>();

        FieldError nameError = validatePlantName(plantName);
        if (nameError != null) {errors.add(nameError);}

        FieldError descriptionError = validatePlantDescription(plantDescription);
        if (descriptionError != null) {errors.add(descriptionError);}

        FieldError countError = validatePlantCount(plantCount);
        if (countError != null) {errors.add(countError);}

        FieldError dateError =  (plantDatePlanted.isEmpty()) ? null : validatePlantDate(plantDatePlanted);
        if (dateError != null) {errors.add(dateError);}

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

    /**
     * Converts a date string from the format "dd/MM/yyyy" to "yyyy-MM-dd"
     * @param dateInput A string representing a date in the format "dd/MM/yyyy"
     * @return A string representing a date in the format "yyyy-MM-dd"
     */
    public static String convertDateFormat(String dateInput) {
        String[] parts = dateInput.split("/");
        if (dateInput.length() < 10) {
//            return "0000-00-00";
            return dateInput;
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
