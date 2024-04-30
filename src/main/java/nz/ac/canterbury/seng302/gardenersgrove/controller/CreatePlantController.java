package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.PlantValidator.*;


/**
 * This sprint boot controller sets up a form to create a new plant
 */
@Controller
public class CreatePlantController {

    Logger logger = LoggerFactory.getLogger(CreatePlantController.class);

    private final PlantService plantService;
    private final GardenService gardenService;

    @Autowired
    public CreatePlantController(PlantService plantService, GardenService gardenService) {
        this.plantService = plantService;
        this.gardenService = gardenService;
    }

    @GetMapping("/create-plant")
    public String form(@RequestParam(name = "gardenID") Long gardenID,
                       @ModelAttribute Plant plant,
                       Model model) {
        logger.info("GET /create-plant");

        if (gardenService.findGarden(gardenID).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not found");
        }
        //Plant plant = new Plant(null, null);

        RedirectService.addEndpoint("/create-plant?gardenID=" + gardenID);

        model.addAttribute("gardenID", gardenID); // Add gardenID to the model
        model.addAttribute("gardens", gardenService.getGardens());
        model.addAttribute("plant", plant);


        Garden ownerGarden = null;
        Optional<Garden> found = gardenService.findGarden(gardenID);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not found");
        }
        ownerGarden = found.get();
        plant.setGarden(ownerGarden); // Set the garden for the plant
        model.addAttribute("plantName", plant.getName());
        model.addAttribute("plantCount", plant.getCount());
        model.addAttribute("plantDescription", plant.getDescription());
        model.addAttribute("datePlanted", plant.getDatePlanted());
        model.addAttribute("lastEndpoint", RedirectService.getPreviousPage());

        return "createPlantFormTemplate";
    }


    /**
     * Submits the form when the user clicks "Create Plant" on the form.
     */
    @PostMapping("/create-plant")
    public String submitForm(
            @RequestParam("gardenID") Long gardenID,
            @RequestParam("datePlanted") String datePlanted,
            @ModelAttribute("plant") Plant plant,
            BindingResult bindingResult,
            Model model) throws Exception {
        logger.info("POST /create-plant");

        String formattedDate;
        formattedDate = convertDateFormat(datePlanted);
        //Validates input fields
        checkName(plant.getName(), bindingResult);
        checkDescription(plant.getDescription(), bindingResult);
        checkCount(plant.getCount(), bindingResult);
        checkDateValidity(formattedDate, bindingResult);

        Garden ownerGarden = null;
        Optional<Garden> found = gardenService.findGarden(gardenID);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not found");
        }
        ownerGarden = found.get();


        plant.setCount(plant.getCount().replace(',', '.'));

        plant.setGarden(ownerGarden); // Set the garden for the plant

        model.addAttribute("lastEndpoint", RedirectService.getPreviousPage());

        model.addAttribute("plantName", plant.getName());
        model.addAttribute("plantCount", plant.getCount());
        model.addAttribute("plantDescription", plant.getDescription());
        model.addAttribute("datePlanted", plant.getDatePlanted());

        if (validatePlantName(plant.getName()) != null) {
            model.addAttribute("nameError", validatePlantName(plant.getName()).getDefaultMessage());
        }

        if (validatePlantCount(plant.getCount()) != null) {
            model.addAttribute("countError", validatePlantCount(plant.getCount()).getDefaultMessage());
        }

        if (validatePlantDescription(plant.getDescription()) != null) {
            model.addAttribute("descriptionError", validatePlantDescription(plant.getDescription()).getDefaultMessage());
        }

        if (validatePlantDate(formattedDate) != null) {
            model.addAttribute("dateError", validatePlantDate(formattedDate).getDefaultMessage());
        }
        // If there are validation errors, return to the form page
        if (model.containsAttribute("nameError") || model.containsAttribute("countError")
                || model.containsAttribute("descriptionError") || model.containsAttribute("dateError")) {
            model.addAttribute("gardenID", gardenID); // Add gardenID to the model before forwarding to error display page
            return "createPlantFormTemplate";
        }else {
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

    private ObjectError checkDateValidity(String date, BindingResult bindingResult) {
        return validatePlantDate(date);
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
