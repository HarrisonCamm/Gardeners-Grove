package nz.ac.canterbury.seng302.gardenersgrove.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
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
        addAttributes(model, session, garden, garden.getId(), garden.getName(), garden.getLocation(), garden.getSize(), garden.getDescription());
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
                             Model model,
                             HttpSession session) throws ResponseStatusException {
        logger.info("PUT /edit-garden");

        Optional<Garden> result = gardenService.findGarden(gardenID);
        User currentUser = userService.getAuthenticatedUser();
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");
        } else if (!result.get().getOwner().equals(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit this garden.");
        }

        Garden currentGarden = result.get();
        String gardenName = garden.getName();
        String gardenSize = garden.getSize();
        String gardenDescription = (garden.getDescription() != null ? garden.getDescription().trim() : "");
        Location gardenLocation = garden.getLocation();
        gardenLocation.setId(currentGarden.getLocation().getId());

        // Perform field validation
        ArrayList<FieldError> errors = checkFields(gardenName, gardenLocation, gardenSize, gardenDescription);

        // Validate garden description
        FieldError descriptionError = validateGardenDescription(gardenDescription);
        if (descriptionError != null) {
            errors.add(descriptionError);
        }

        session.setAttribute("gardenID", gardenID);
        addAttributes(model, session, currentGarden, currentGarden.getId(), gardenName, gardenLocation, gardenSize, gardenDescription);

        boolean isAppropriate = moderationService.isContentAppropriate(gardenDescription);
        if (!isAppropriate) {
            errors.add(new FieldError("garden", "description", "The description does not match the language standards of the app"));
        }

        if (!errors.isEmpty()) {
            // If there are validation errors, return to the form page
            for (FieldError error : errors) {
                model.addAttribute(error.getField().replace('.', '_') + "Error", error.getDefaultMessage());
            }
            model.addAttribute("garden", currentGarden);
            return "editGardenTemplate"; // Use the correct form template name here
        } else {
            gardenService.updateGarden(currentGarden, gardenName, gardenLocation, gardenSize, garden.getIsPublic(), gardenDescription);
            return "redirect:/view-garden?gardenID=" + currentGarden.getId();
        }
    }



    @PatchMapping("/edit-garden")
    public ResponseEntity changePublicity(@RequestParam("gardenID") Long gardenID,
                                          @RequestParam("isPublic") Boolean isPublic,
                                          Model model,
                                          HttpSession session){
        logger.info("PATCH /edit-garden");

        Optional<Garden> garden = gardenService.findGarden(gardenID);

        User currentUser = userService.getAuthenticatedUser();
        if (garden.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");
        else if (!garden.get().getOwner().equals(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit this garden.");

        garden.get().setIsPublic(isPublic);
        gardenService.addGarden(garden.get());

        return ResponseEntity.ok().build();
    }

    /**
     * Checks the garden name, location and size for errors
     *
     * @param gardenName     Garden name
     * @param gardenLocation Garden location
     * @param gardenSize     Garden size
     */
    private ArrayList<FieldError> checkFields(String gardenName, Location gardenLocation, String gardenSize, String gardenDescription) {

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


    public void addAttributes(Model model,HttpSession session,  Garden garden, Long gardenID, String gardenName, Location gardenLocation, String gardenSize, String gardenDescription) {
        model.addAttribute("id", gardenID);

        model.addAttribute("name", gardenName);
        model.addAttribute("description", gardenDescription);


        model.addAttribute("garden", garden);
        model.addAttribute("gardenID", session.getAttribute("gardenID"));
        model.addAttribute("location.streetAddress", gardenLocation.getStreetAddress());
        model.addAttribute("location.suburb", gardenLocation.getSuburb());
        model.addAttribute("location.city", gardenLocation.getCity());
        model.addAttribute("location.postcode", gardenLocation.getPostcode());
        model.addAttribute("location.country", gardenLocation.getCountry());

        model.addAttribute("size", gardenSize);

        model.addAttribute("gardens", gardenService.getOwnedGardens(garden.getOwner().getUserId()));
    }
}
