package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.parser.HttpParser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.PlantValidator.*;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidator.*;


/**
 * This sprint boot controller sets up a form to create a new plant
 */
@Controller
public class CreatePlantController {

    Logger logger = LoggerFactory.getLogger(CreatePlantController.class);

    private final PlantService plantService;
    private final GardenService gardenService;
    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    public CreatePlantController(PlantService plantService, GardenService gardenService, UserService userService, ImageService imageService) {
        this.plantService = plantService;
        this.gardenService = gardenService;
        this.userService = userService;
        this.imageService = imageService;
    }

    @GetMapping("/create-plant")
    public String form(@RequestParam(name = "gardenID") Long gardenID,
                       @ModelAttribute Plant plant,
                       Model model, HttpSession session) {
        logger.info("GET /create-plant");
        RedirectService.addEndpoint("/create-plant?gardenID=" + gardenID);
        session.setAttribute("gardenID", gardenID);
        User currentUser = userService.getAuthenicatedUser();
        Optional<Garden> foundGarden = gardenService.findGarden(gardenID);
        if (foundGarden.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not found");
        } else if (!foundGarden.get().getOwner().equals(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot create a plant for this garden.");

        Plant sessionPlant = (Plant) session.getAttribute("plant");
        if (sessionPlant != null) {
            plant = sessionPlant;
        }
        model.addAttribute("gardens", gardenService.getGardens());
        model.addAttribute("plant", plant);

        addErrors(session, model);
        Garden ownerGarden = foundGarden.get();
        plant.setGarden(ownerGarden); // Set the garden for the plant
        model.addAttribute("gardenID", session.getAttribute("gardenID"));
        model.addAttribute("name", session.getAttribute("name"));
        model.addAttribute("description", session.getAttribute("description"));
        model.addAttribute("count", session.getAttribute("count"));
        model.addAttribute("datePlanted", session.getAttribute("datePlanted"));

//        model.addAttribute("imageError", "");

        // Remove attributes from the session
        session.removeAttribute("name");
        session.removeAttribute("count");
        session.removeAttribute("description");
        session.removeAttribute("datePlanted");

        return "createPlantFormTemplate";
    }

    /**
     * Moves all errors, if any, from the session to the model
     *
     * @param session The http session
     * @param model The model
     */
    private void addErrors(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
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

        User currentUser = userService.getAuthenicatedUser();
        Optional<Garden> foundGarden = gardenService.findGarden(gardenID);
        if (foundGarden.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not found");
        } else if (!foundGarden.get().getOwner().equals(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot create a plant for this garden.");
        Garden ownerGarden = foundGarden.get();

        // Validates input fields
        Plant sessionPlant = (Plant) session.getAttribute("plant");

        if (sessionPlant != null) {
            session.removeAttribute("plant");
        }

        String formattedDate;
        formattedDate = convertDateFormat(datePlanted);

        Image image = Image.removeTemporaryImage(session, imageService);
        if (image == null) {
            try {
                MultipartFile imageFile = (MultipartFile) session.getAttribute("imageFile");
                if (imageFile != null) {
                    image = new Image(imageFile, false);
                    session.removeAttribute("imageFile");
                } else {
                    Path imagePath = Paths.get(resourceLoader.getResource("classpath:static/images/leaves-80x80.png").getURI());
                    image = new Image(Files.readAllBytes(imagePath), "png", false);
                }
            } catch (Exception e) {
                logger.error("Failed to set plant image from file", e);
            }
        } else {
            image.makePermanent();
        }
        plant.setImage(image);

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
        session.setAttribute("gardenID", plant.getGarden().getId());

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
        } else {
            plantService.addPlant(plant);
            session.removeAttribute("name");
            session.removeAttribute("count");
            session.removeAttribute("description");
            session.removeAttribute("datePlanted");
            return "redirect:/view-garden?gardenID=" + plant.getGarden().getId();
        }
    }

    /**
     * Saves the image file into the session if the temporary image cannot be read later (if expired)
     *
     * @param gardenID The ID of the garden
     * @param file The image file
     * @param session The http session
     * @return A redirect to this same page to refresh it
     */
    @PostMapping("/create-plant-picture")
    public String uploadImage(@RequestParam("gardenID") Long gardenID,
                              @RequestParam("file") MultipartFile file,
                              HttpSession session) {
        logger.info("POST /create-plant-picture");
        Garden garden = gardenService.findGarden(gardenID).get();

        Plant plant = new Plant(garden, "", "", "", "", Image.getTemporaryImage(session));

        // Add the plant object to the session
        session.setAttribute("plant", plant);
        session.setAttribute("imageFile", file);

        return "redirect:/create-plant?gardenID=" + plant.getGarden().getId();
    }

}
