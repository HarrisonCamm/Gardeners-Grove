package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.GardenValidator.*;

/**
 * This sprint boot controller sets up a form to edit an existing garden
 */
@Controller
public class EditGardenController {

    Logger logger = LoggerFactory.getLogger(EditGardenController.class);

    private final GardenService gardenService;

//    @Autowired
    public EditGardenController(GardenService gardenService) { this.gardenService = gardenService; }

    /**
     * If the form has been previously filled, load the attributes back from the previous POST request
     *
     * @param model Spring Boot model
     * @return HTML ThymeLeaf template file name
     */
    @GetMapping("/edit-garden")
    public String form(@RequestParam("gardenID") Long gardenID,
                       Model model) throws ResponseStatusException {
        logger.info("GET /edit-garden");
        RedirectService.addEndpoint("/edit-garden?gardenID=" + gardenID);

        Optional<Garden> result = gardenService.findGarden(gardenID);
        if (result.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");

        Garden garden = result.get();
        addAttributes(model, garden, garden.getId(), garden.getName(), garden.getLocation(), garden.getSize());
        return "editGardenTemplate";
    }

    /**
     * Submits the form when the user clicks "Create Garden" on the form.
     * Redirects the user to the garden information screen
     * @param model Model part of MVC pattern
     * @return Redirect object
     */
    @PutMapping("/edit-garden")
    public String submitForm(@RequestParam("gardenID") Long gardenID,
                             @ModelAttribute Garden garden,
                             BindingResult bindingResult,
                             Model model) throws ResponseStatusException {
        logger.info("PUT /edit-garden");

        Optional<Garden> result = gardenService.findGarden(gardenID);
        if (result.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");
        String gardenName = garden.getName();
        String gardenSize = garden.getSize();
        Location gardenLocation = garden.getLocation();
        // Perform validation
        checkFields(gardenName, gardenLocation, gardenSize, bindingResult);
        garden.setId(result.get().getId());

        addAttributes(model, garden, garden.getId(), gardenName, gardenLocation, gardenSize);

        if (bindingResult.hasErrors()) {
            // If there are validation errors, return to the form page
            return "editGardenTemplate";
        } else {
            gardenService.addGarden(garden);
            return "redirect:/view-garden?gardenID=" + garden.getId();
        }
    }

    /**
     * Checks the garden name, location and size for errors
     * @param gardenName Garden name
     * @param gardenLocation Garden location
     * @param gardenSize Garden size
     * @param bindingResult Object to add errors to for Thyme leaf
     */
    public void checkFields(String gardenName, Location gardenLocation, String gardenSize, BindingResult bindingResult) {
        ObjectError nameError = validateGardenName(gardenName);
        if (nameError != null) {
            bindingResult.addError(nameError);
        }

        ObjectError locationCityError = validateGardenLocation(gardenLocation, true);
        if (locationCityError != null) {
            bindingResult.addError(locationCityError);
        }

        ObjectError locationCountryError = validateGardenLocation(gardenLocation, false);
        if (locationCountryError != null) {
            bindingResult.addError(locationCountryError);
        }

        ObjectError sizeError = validateSize(gardenSize);
        if (sizeError != null) {
            bindingResult.addError(sizeError);
        }
    }

    public void addAttributes(Model model, Garden garden, Long gardenID, String gardenName, Location gardenLocation, String gardenSize) {
        model.addAttribute("id", gardenID);

        model.addAttribute("name", gardenName);

        model.addAttribute("garden", garden);

        model.addAttribute("location.streetAddress", gardenLocation.getStreetAddress());
        model.addAttribute("location.suburb", gardenLocation.getSuburb());
        model.addAttribute("location.city", gardenLocation.getCity());
        model.addAttribute("location.postcode", gardenLocation.getPostcode());
        model.addAttribute("location.country", gardenLocation.getCountry());

        model.addAttribute("size", gardenSize);

        model.addAttribute("gardens", gardenService.getGardens());
    }

}
