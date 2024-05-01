package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.GardenValidator.*;

/**
 * This sprint boot controller sets up a form to create a new garden
 */
@Controller
public class CreateGardenController {

    Logger logger = LoggerFactory.getLogger(CreateGardenController.class);

    private final GardenService gardenService;
    private final LocationService locationService;
    private final UserService userService;

    @Autowired
    public CreateGardenController(GardenService gardenService, LocationService locationService, UserService userService) {
        this.gardenService = gardenService;
        this.locationService = locationService;
        this.userService = userService;
    }

    /**
     * If the form has been previously filled, load the attributes back from the previous POST request
     * @param model Spring Boot model
     * @return HTML ThymeLeaf template file name
     */
    @GetMapping("/create-garden")
    public String form(@ModelAttribute Garden garden,
                       Model model) {
        logger.info("GET /create-garden");

        User currentUser = userService.getAuthenicatedUser();

        Location gardenLocation = new Location("", "", "", "", ""); //Bad code warning
        garden.setLocation(gardenLocation); //avoiding NullPointException

        String gardenName = garden.getName();
        String gardenSize = garden.getSize();
        addAttributes(model, currentUser.getUserId(), gardenName, gardenLocation, gardenSize);

        RedirectService.addEndpoint("/create-garden");

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
    public String submitForm(@RequestParam(name="name") String gardenName,
                             @RequestParam(name="location.streetAddress", required = false) String streetAddress,
                            @RequestParam(name="location.suburb", required = false) String suburb,
                            @RequestParam(name="location.city") String city,
                            @RequestParam(name="location.postcode", required = false) String postcode,
                            @RequestParam(name="location.country") String country,
                            @RequestParam(name="size", required = false) String gardenSize,
                             Model model) {
        logger.info("POST /create-garden");


        User currentUser = userService.getAuthenicatedUser();
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        Location gardenLocation = new Location(streetAddress, suburb, city, postcode, country);
        Garden garden = new Garden(gardenName, gardenLocation, gardenSize);
        garden.setOwner(currentUser);

        // Perform validation
        ArrayList<FieldError> errors = checkFields(gardenName, gardenLocation, gardenSize);

        addAttributes(model, currentUser.getUserId(), gardenName, gardenLocation, gardenSize);

        if (!errors.isEmpty()) {
            // If there are validation errors, return to the form page
            for (FieldError error : errors) {
                model.addAttribute(error.getField().replace('.', '_') + "Error", error.getDefaultMessage());
            }
            model.addAttribute("garden", garden);
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
     *
     * @param gardenName     Garden name
     * @param gardenLocation Garden location
     * @param gardenSize     Garden size
     */
    public ArrayList<FieldError> checkFields(String gardenName, Location gardenLocation, String gardenSize) {

        ArrayList<FieldError> errors = new ArrayList<>();

        FieldError nameError = validateGardenName(gardenName);
        if (nameError != null) {
            errors.add(nameError);
        }

        FieldError locationCityError = validateGardenLocation(gardenLocation, true);
        if (locationCityError != null) {
            errors.add(locationCityError);
        }

        FieldError locationCountryError = validateGardenLocation(gardenLocation, false);
        if (locationCountryError != null) {
            errors.add(locationCountryError);
        }

        FieldError sizeError = validateSize(gardenSize);
        if (sizeError != null) {
            errors.add(sizeError);
        }
        return errors;
    }

    /**
     * Adds strings to the model
     * @param model model part of MVC
     * @param gardenName garden name
     * @param gardenLocation garden location object
     * @param gardenSize garden size
     */
    public void addAttributes(Model model, Long userId, String gardenName, Location gardenLocation, String gardenSize) {

        model.addAttribute("lastEndpoint", RedirectService.getPreviousPage());

        model.addAttribute("name", gardenName);

        model.addAttribute("location.streetAddress", gardenLocation.getStreetAddress());
        model.addAttribute("location.suburb", gardenLocation.getSuburb());
        model.addAttribute("location.city", gardenLocation.getCity());
        model.addAttribute("location.postcode", gardenLocation.getPostcode());
        model.addAttribute("location.country", gardenLocation.getCountry());

        model.addAttribute("size", gardenSize);

        model.addAttribute("gardens", gardenService.getOwnedGardens(userId));
    }

}
