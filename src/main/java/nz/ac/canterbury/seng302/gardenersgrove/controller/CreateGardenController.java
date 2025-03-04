package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
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
import java.util.List;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.GardenValidator.*;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.LocationValidator.*;
import java.util.logging.Level;



/**
 * This sprint boot controller sets up a form to create a new garden
 */
@Controller
public class CreateGardenController {

    Logger logger = LoggerFactory.getLogger(CreateGardenController.class);

    private final GardenService gardenService;
    private final LocationService locationService;
    private final UserService userService;
    private final ModerationService moderationService;

    @Autowired
    public CreateGardenController(GardenService gardenService, LocationService locationService,
                                  UserService userService, ModerationService moderationService) {
        this.gardenService = gardenService;
        this.locationService = locationService;
        this.userService = userService;
        this.moderationService = moderationService;
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

        User currentUser = userService.getAuthenticatedUser();

        Location gardenLocation = new Location("", "", "", "", ""); //Bad code warning
        garden.setLocation(gardenLocation); //avoiding NullPointException

        String gardenName = garden.getName();
        String gardenSize = garden.getSize();
        addAttributes(model, currentUser.getUserId(), gardenName, gardenLocation, gardenSize, garden.getDescription());

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
                            @RequestParam(name="description", required = false) String gardenDescription,
                            @RequestParam(name="size", required = false) String gardenSize,
                             Model model) {
        logger.info("POST /create-garden");

        User currentUser = userService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        Location gardenLocation = new Location(streetAddress, suburb, city, postcode, country);
        Garden garden = new Garden(gardenName, gardenLocation, gardenSize, currentUser, gardenDescription);

        // Perform validation, get back all errors
        List<FieldError> errors = checkFields(gardenName, gardenLocation, gardenSize, gardenDescription);

        addAttributes(model, currentUser.getUserId(), gardenName, gardenLocation, gardenSize, gardenDescription);

        boolean isAppropriate = moderationService.isContentAppropriate(gardenDescription);
        if (!isAppropriate) {
            errors.add(new FieldError("garden", "description", "The description does not match the language standards of the app"));
        }

        if (!errors.isEmpty()) {
            // If there are validation errors, return to the form page
            for (FieldError error : errors) {
                model.addAttribute(error.getField().replace('.', '_') + "Error", error.getDefaultMessage());
            }
            model.addAttribute("garden", garden);
            return "createGardenFormTemplate";
        } else {
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
    private List<FieldError> checkFields(String gardenName, Location gardenLocation, String gardenSize, String gardenDescription) {

        // List for all the errors
        ArrayList<FieldError> errors = new ArrayList<>();

        // Validates Garden Name
        FieldError nameError = validateGardenName(gardenName);

        FieldError descriptionError = validateGardenDescription(gardenDescription);
        if (descriptionError != null) {
            errors.add(descriptionError);
        }

        // Check for name error and display
        if (nameError != null) {
            errors.add(nameError);
        }

        // Valid the location fields in Location
        List<FieldError> locationErrors = validateGardenLocation(gardenLocation);
        errors.addAll(locationErrors);


        FieldError sizeError = validateSize(gardenSize);
        if (sizeError != null) {
            errors.add(sizeError);
        }
        return errors;
    }

    private List<FieldError> validateGardenLocation(Location gardenLocation) {
        List<FieldError> errors = new ArrayList<>();

        FieldError cityError = validateCity(gardenLocation.getCity());
        if (cityError != null) {
            errors.add(cityError);
        }

        FieldError countryError = validateCountry(gardenLocation.getCountry());
        if (countryError != null) {
            errors.add(countryError);
        }

        FieldError streetAddressError = validateStreetAddress(gardenLocation.getStreetAddress());
        if (streetAddressError != null) {
            errors.add(streetAddressError);
        }

        FieldError suburbError = validateSuburb(gardenLocation.getSuburb());
        if (suburbError != null) {
            errors.add(suburbError);
        }

        FieldError postCodeError = validatePostcode(gardenLocation.getPostcode());
        if (postCodeError != null) {
            errors.add(postCodeError);
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
    public void addAttributes(Model model, Long userId, String gardenName, Location gardenLocation, String gardenSize, String gardenDescription) {

        model.addAttribute("lastEndpoint", RedirectService.getPreviousPage());

        model.addAttribute("name", gardenName);
        model.addAttribute("description", gardenDescription);

        model.addAttribute("location.streetAddress", gardenLocation.getStreetAddress());
        model.addAttribute("location.suburb", gardenLocation.getSuburb());
        model.addAttribute("location.city", gardenLocation.getCity());
        model.addAttribute("location.postcode", gardenLocation.getPostcode());
        model.addAttribute("location.country", gardenLocation.getCountry());

        model.addAttribute("size", gardenSize);

        model.addAttribute("gardens", gardenService.getOwnedGardens(userId));
    }

}
