package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.apache.tomcat.util.http.parser.HttpParser;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
                       Model model, HttpSession session) {
        logger.info("GET /create-plant");

        if (gardenService.findGarden(gardenID).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not found");
        }
        Plant sessionPlant = (Plant) session.getAttribute("plant");
        if (sessionPlant != null) {
            plant = sessionPlant;
        }
        model.addAttribute("gardenID", gardenID); // Add gardenID to the model
        model.addAttribute("gardens", gardenService.getGardens());
        model.addAttribute("plant", plant);


        Garden ownerGarden = null;
        Optional<Garden> found = gardenService.findGarden(gardenID);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not found");
        }
        addErrors(session, model);
        ownerGarden = found.get();
        plant.setGarden(ownerGarden); // Set the garden for the plant
        model.addAttribute("name", session.getAttribute("name"));
        model.addAttribute("description", session.getAttribute("description"));
        model.addAttribute("count", session.getAttribute("count"));
        model.addAttribute("datePlanted", session.getAttribute("datePlanted"));
        model.addAttribute("lastEndpoint", RedirectService.getPreviousPage());

        // Remove attributes from the session
        session.removeAttribute("name");
        session.removeAttribute("count");
        session.removeAttribute("description");
        session.removeAttribute("datePlanted");

        RedirectService.addEndpoint("/create-plant?gardenID=" + gardenID);

        return "createPlantFormTemplate";
    }

    private void addErrors(HttpSession session, Model model) {
        HashMap<String, String> errors = (HashMap<String, String>) session.getAttribute("errors");
        if (errors != null) {
            for (Map.Entry<String, String> entry : errors.entrySet()) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
            session.removeAttribute("errors");
        }
    }


    /**
     * Submits the form when the user clicks "Create Plant" on the form.
     */
    @PostMapping("/create-plant")
    public String submitForm(
            @RequestParam("gardenID") Long gardenID,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("count") String count,
            @RequestParam("datePlanted") String datePlanted,
            @ModelAttribute("plant") Plant plant,
            BindingResult bindingResult,
            HttpSession session,
            Model model) throws Exception {
        logger.info("POST /create-plant");

        Plant sessionPlant = (Plant) session.getAttribute("plant");

        if (sessionPlant != null) {
            plant.setImage(sessionPlant.getImage());
            plant.setPicture(sessionPlant.getPicture());
            session.removeAttribute("plant");
        }

        String formattedDate;
        formattedDate = convertDateFormat(datePlanted);
        //Validates input fields
        checkName(plant.getName(), bindingResult);
        checkDescription(plant.getDescription(), bindingResult);
        checkCount(plant.getCount(), bindingResult);
        checkDateValidity(formattedDate, bindingResult);

        if (plant.getPicture() == null) {
            plant.setPicture("leaves-80x80.png"); // Set default picture

            Path imagePath = Paths.get("src/main/resources/static/images/leaves-80x80.png");
            try {
                plant.setImage(Files.readAllBytes(imagePath));
            } catch (IOException e) {
                logger.error("Failed to set default image", e);
            }
        }
        Garden ownerGarden = null;
        Optional<Garden> found = gardenService.findGarden(gardenID);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not found");
        }
        ownerGarden = found.get();


        plant.setCount(plant.getCount().replace(',', '.'));

        plant.setGarden(ownerGarden); // Set the garden for the plant

        model.addAttribute("lastEndpoint", RedirectService.getPreviousPage());

        model.addAttribute("gardenID", gardenID); // Add gardenID to the model
        model.addAttribute("gardens", gardenService.getGardens());
        model.addAttribute("plant", plant);

        session.setAttribute("name", name);
        session.setAttribute("count", count);
        session.setAttribute("description", description);
        session.setAttribute("datePlanted", plant.getDatePlanted());


//        model.addAttribute("plantName", plant.getName());
//        model.addAttribute("plantCount", plant.getCount());
//        model.addAttribute("plantDescription", plant.getDescription());
//        model.addAttribute("datePlanted", formattedDate);

        Map<String, String> errors = new HashMap<>();

        if (validatePlantName(plant.getName()) != null) {
            errors.put("nameError", Objects.requireNonNull(validatePlantName(plant.getName())).getDefaultMessage());
        }

        if (validatePlantCount(plant.getCount()) != null) {
            errors.put("countError", Objects.requireNonNull(validatePlantCount(plant.getCount())).getDefaultMessage());
        }

        if (validatePlantDescription(plant.getDescription()) != null) {
            errors.put("descriptionError", Objects.requireNonNull(validatePlantDescription(plant.getDescription())).getDefaultMessage());
        }

        if (!datePlanted.isEmpty() && validatePlantDate(formattedDate) != null) {
            errors.put("dateError", Objects.requireNonNull(validatePlantDate(formattedDate)).getDefaultMessage());
        }

        session.setAttribute("errors", errors);
        // If there are validation errors, return to the form page
        if (errors.containsKey("nameError") || errors.containsKey("countError")
                || errors.containsKey("descriptionError") || errors.containsKey("dateError")) {
            model.addAttribute("gardenID", gardenID); // Add gardenID to the model before forwarding to error display page
            return "redirect:/create-plant?gardenID=" + gardenID;
        }else {
            plantService.addPlant(plant);
            return "redirect:/view-garden?gardenID=" + plant.getGarden().getId();
        }
    }

    @PostMapping("/create-plant-picture")
    public String uploadImage(@RequestParam("gardenID") Long gardenID,
                              @RequestParam("file") MultipartFile file,
                              HttpSession session) throws IOException, ParseException {
        logger.info("POST /create-plant-picture");
        Garden garden = gardenService.findGarden(gardenID).get();

        Plant plant = new Plant(garden, "", "", "", "", file.getOriginalFilename());
        plant.setImage(file.getBytes());

        // Add the plant object to the session
        session.setAttribute("plant", plant);

        return "redirect:/create-plant?gardenID=" + plant.getGarden().getId();
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
