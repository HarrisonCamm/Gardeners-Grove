package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.GardenValidator.*;

/**
 * This sprint boot controller sets up a form to create a new garden
 */
@Controller
public class CreateGardenController {

    Logger logger = LoggerFactory.getLogger(CreateGardenController.class);

    private final GardenService gardenService;
    private final LocationService locationService;

    @Autowired
    public CreateGardenController(GardenService gardenService, LocationService locationService) {
        this.gardenService = gardenService;
        this.locationService = locationService;
    }


//    @GetMapping("/")
//    public String redirect() {
//        logger.info("GET /");
//        RedirectService.addEndpoint("/");
//        return "redirect:/create-garden";
//    }

    /**
     * If the form has been previously filled, load the attributes back from the previous POST request
     * @param model Spring Boot model
     * @return HTML ThymeLeaf template file name
     */
    @GetMapping("/create-garden")
    public String form(@ModelAttribute Garden garden,
                       Model model) {
        logger.info("GET /create-garden");

        Location gardenLocation = new Location("", "", "", "", ""); //Bad code warning
        garden.setLocation(gardenLocation); //avoiding NullPointException

        RedirectService.addEndpoint("/create-garden");

        String gardenName = garden.getName();
        String gardenSize = garden.getSize();
        addAttributes(model, gardenName, gardenLocation, gardenSize);
        return "createGardenFormTemplate";
    }

    @GetMapping("/Cancel")
    public String cancel() {
        String prevUrl = RedirectService.getPreviousPage();
        return "redirect:" + prevUrl;
    }


    /**
     * Submits the form when the user clicks "Create Garden" on the form.
     * Redirects the user to the garden information screen
     * @param model Model part of MVC pattern
     * @return Redirect object
     */
    @PostMapping("/create-garden")
    public String submitForm(@ModelAttribute Garden garden,
                             BindingResult bindingResult,
                             Model model) {
        logger.info("POST /create-garden");

        String gardenName = garden.getName();
        String gardenSize = garden.getSize();
        Location gardenLocation = garden.getLocation();
        // Perform validation
        checkFields(gardenName, gardenLocation, gardenSize, bindingResult);

        addAttributes(model, gardenName, gardenLocation, gardenSize);

        if (bindingResult.hasErrors()) {
            // If there are validation errors, return to the form page
            for (ObjectError error : bindingResult.getAllErrors()) {
                model.addAttribute(error.getObjectName(), true);
            }
            return "createGardenFormTemplate";
        } else {
            //TODO figure out how to not have duplicate locations. Probably next sprint tbh
            locationService.addLocation(garden.getLocation());
            gardenService.addGarden(garden);
            return "redirect:/view-garden?gardenID=" +garden.getId();
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

    /**
     * Adds strings to the model
     * @param model model part of MVC
     * @param gardenName garden name
     * @param gardenLocation garden location object
     * @param gardenSize garden size
     */
    public void addAttributes(Model model, String gardenName, Location gardenLocation, String gardenSize) {
        model.addAttribute("name", gardenName);

        model.addAttribute("location.streetAddress", gardenLocation.getStreetAddress());
        model.addAttribute("location.suburb", gardenLocation.getSuburb());
        model.addAttribute("location.city", gardenLocation.getCity());
        model.addAttribute("location.postcode", gardenLocation.getPostcode());
        model.addAttribute("location.country", gardenLocation.getCountry());

        model.addAttribute("size", gardenSize);

        model.addAttribute("gardens", gardenService.getGardens());
    }




}
