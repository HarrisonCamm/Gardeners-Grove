package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.GardenValidator.*;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.LocationValidator.*;

/**
 * This sprint boot controller sets up a form to edit an existing garden
 */
@Controller
public class EditGardenController {

    Logger logger = LoggerFactory.getLogger(EditGardenController.class);

    private final GardenService gardenService;
    private final UserService userService;
    private final ModerationService moderationService;

    @Autowired
    public EditGardenController(GardenService gardenService, UserService userService, ModerationService moderationService) {
        this.gardenService = gardenService;
        this.userService = userService;
        this.moderationService = moderationService;
    }

    /**
     * If the form has been previously filled, load the attributes back from the previous POST request
     *
     * @param model Spring Boot model
     * @return HTML ThymeLeaf template file name
     */
    @GetMapping("/edit-garden")
    public String form(@RequestParam("gardenID") Long gardenID,
                       Model model,
                       HttpSession session) throws ResponseStatusException {
        logger.info("GET /edit-garden");
        RedirectService.addEndpoint("/edit-garden?gardenID=" + gardenID);
        Optional<Garden> result = gardenService.findGarden(gardenID);
        User currentUser = userService.getAuthenticatedUser();
        if (result.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");
        else if (!result.get().getOwner().equals(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit this garden.");

        Garden garden = result.get();
        model.addAttribute("lastEndpoint", RedirectService.getPreviousPage());
        session.setAttribute("gardenID", gardenID);
        addAttributes(model, garden, garden.getId(), garden.getName(), garden.getLocation(), garden.getSize(), garden.getDescription());
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
                             @RequestParam("name") String gardenName,
                             @RequestParam(name = "location.streetAddress", required = false) String streetAddress,
                             @RequestParam(name = "location.suburb", required = false) String suburb,
                             @RequestParam(name = "location.city") String city,
                             @RequestParam(name = "location.postcode", required = false) String postcode,
                             @RequestParam(name = "location.country") String country,
                             @RequestParam(name = "description", required = false) String gardenDescription,
                             @RequestParam(name = "size", required = false) String gardenSize,
                             HttpSession session,
                             Model model,
                             HttpServletResponse response) throws ResponseStatusException {
        logger.info("PUT /edit-garden");

        Optional<Garden> result = gardenService.findGarden(gardenID);
        User currentUser = userService.getAuthenticatedUser();
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");
        } else if (!result.get().getOwner().equals(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit this garden.");
        }

        Garden currentGarden = result.get();
        currentGarden.setName(gardenName);
        currentGarden.setSize(gardenSize);
        currentGarden.setDescription(gardenDescription != null ? gardenDescription.trim() : "");

        Location gardenLocation = currentGarden.getLocation();
        gardenLocation.setStreetAddress(streetAddress);
        gardenLocation.setSuburb(suburb);
        gardenLocation.setCity(city);
        gardenLocation.setPostcode(postcode);
        gardenLocation.setCountry(country);

        List<FieldError> errors = checkFields(gardenName, gardenLocation, gardenSize, gardenDescription);

        model.addAttribute("gardenID", gardenID);
        model.addAttribute("garden", currentGarden);
        model.addAttribute("lastEndpoint", RedirectService.getPreviousPage());
        addAttributes(model, currentGarden, gardenID, gardenName, gardenLocation, gardenSize, gardenDescription);

        boolean isAppropriate = moderationService.isContentAppropriate(gardenDescription);
        if (!isAppropriate) {
            errors.add(new FieldError("garden", "description", "The description does not match the language standards of the app"));
        }

        if (!errors.isEmpty()) {
            for (FieldError error : errors) {
                model.addAttribute(error.getField().replace('.', '_') + "Error", error.getDefaultMessage());
            }
            model.addAttribute("garden", currentGarden);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "editGardenTemplate";
        } else {
            gardenService.updateGarden(currentGarden, gardenName, gardenLocation, gardenSize, currentGarden.getIsPublic(), gardenDescription);
            return "redirect:/view-garden?gardenID=" + currentGarden.getId();
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

        ArrayList<FieldError> errors = new ArrayList<>();

        FieldError nameError = validateGardenName(gardenName);
        if (nameError != null) {
            errors.add(nameError);
        }

        FieldError descriptionError = validateGardenDescription(gardenDescription);
        if (descriptionError != null) {
            errors.add(descriptionError);
        }

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


    public void addAttributes(Model model, Garden garden, Long gardenID, String gardenName, Location gardenLocation, String gardenSize, String gardenDescription) {
        model.addAttribute("id", gardenID);
        model.addAttribute("name", gardenName);
        model.addAttribute("description", gardenDescription);
        model.addAttribute("garden", garden);
        model.addAttribute("gardenID", gardenID);
        model.addAttribute("location.streetAddress", gardenLocation.getStreetAddress());
        model.addAttribute("location.suburb", gardenLocation.getSuburb());
        model.addAttribute("location.city", gardenLocation.getCity());
        model.addAttribute("location.postcode", gardenLocation.getPostcode());
        model.addAttribute("location.country", gardenLocation.getCountry());

        model.addAttribute("size", gardenSize);

        model.addAttribute("gardens", gardenService.getOwnedGardens(garden.getOwner().getUserId()));
    }
}
