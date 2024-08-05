package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
public class ViewGardenController {
    Logger logger = LoggerFactory.getLogger(ViewGardenController.class);
    private final ImageService imageService;
    private final GardenService gardenService;
    private final PlantService plantService;
    private final UserService userService;
    private final WeatherService weatherService;
    private final TagService tagService;
    private final ModerationService moderationService;
    private final AlertService alertService;

    @Autowired
    public ViewGardenController(GardenService gardenService, PlantService plantService,
                                UserService userService, ImageService imageService,
                                TagService tagService, WeatherService weatherService,
                                ModerationService moderationService, AlertService alertService) {
        this.gardenService = gardenService;
        this.plantService = plantService;
        this.userService = userService;
        this.imageService = imageService;
        this.tagService = tagService;
        this.weatherService = weatherService;
        this.moderationService = moderationService;
        this.alertService = alertService;
    }

    @GetMapping("/view-garden")
    public String viewGarden(@RequestParam("gardenID") Long gardenID,
                             HttpSession session,
                             Model model,
                             HttpServletResponse response) {
        // Add cache control headers
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        logger.info("GET /view-garden");
        RedirectService.addEndpoint("/view-garden?gardenID=" + gardenID);

        Image.removeTemporaryImage(session, imageService);
        session.removeAttribute("imageFile");

        Optional<Garden> garden = gardenService.findGarden(gardenID);
        User currentUser = userService.getAuthenticatedUser();
        if (garden.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");
        else if (!garden.get().getOwner().equals(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view this garden.");


        addAttributes(currentUser, gardenID, model, plantService, gardenService);
        return "viewGardenDetailsTemplate";
    }

    /**
     * @param plantID  The ID of the plant to add the picture to
     * @param gardenID The ID of the garden the plant is in
     * @param file     The image file to upload
     * @param model    The Spring Boot model
     * @return The view garden page
     */
    @PostMapping("/view-garden")
    public String addPlantPicture(@RequestParam("plantID") Long plantID,
                                  @RequestParam("gardenID") Long gardenID,
                                  @RequestParam("file") MultipartFile file,
                                  HttpSession session,
                                  Model model) {
        logger.info("POST /view-garden");

        Optional<Garden> garden = gardenService.findGarden(gardenID);
        User currentUser = userService.getAuthenticatedUser();
        if (garden.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");
        else if (!garden.get().getOwner().equals(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view this garden.");

        Optional<Plant> foundPlant = plantService.findPlant(plantID);
        if (foundPlant.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant with ID " + plantID + " not present");
        Plant plant = foundPlant.get();

        try {
            Image image = Image.removeTemporaryImage(session, imageService);
            image = (image == null ? new Image(file, false) : image.makePermanent());

            Image oldImage = plant.getImage();
            plant.setImage(image);
            plantService.addPlant(plant);
            if (oldImage != null) {
                imageService.deleteImage(oldImage);
            }
        } catch (Exception e) {
            logger.error("Failed to upload new plant image", e);
        }

        addAttributes(currentUser, gardenID, model, plantService, gardenService);
        return "redirect:/view-garden?gardenID=" + gardenID;
    }

    @PostMapping("/add-tag")
    public String addTag(@RequestParam("gardenID") Long gardenID,
                         @RequestParam("tag") String tag,
                         Model model) {
        logger.info("POST /add-tag");

        Optional<Garden> garden = gardenService.findGarden(gardenID);
        User currentUser = userService.getAuthenticatedUser();
        if (garden.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");
        else if (!garden.get().getOwner().equals(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view this garden.");

        // Moderate the tag before adding
        String possibleTerms = moderationService.moderateText(tag);

        if (!possibleTerms.equals("null")) {
            // Add attributes and return the same view
            addAttributes(currentUser, gardenID, model, plantService, gardenService);

            // Get weather information
            WeatherResponse weatherResponse = weatherService.getCurrentWeather(garden.get().getLocation().getCity(), garden.get().getLocation().getCountry());
            logger.info("Weather response: " + weatherResponse.toString());
            model.addAttribute("weatherResponse", weatherResponse);

            // Show error
            model.addAttribute("tagError", "Profanity or inappropriate language detected");

            return "viewGardenDetailsTemplate";
        } else {
            // Tag is ok, Add tag to the database and to the garden's list of tags
            Tag addedTag = tagService.addTag(new Tag(tag));
            gardenService.addTagToGarden(gardenID, addedTag);
        }

        // Add attributes
        addAttributes(currentUser, gardenID, model, plantService, gardenService);

        // Return user to page
        return "redirect:/view-garden?gardenID=" + gardenID;
    }

    @PostMapping("/dismiss-alert")
    public String dismissAlert(@RequestParam("alertType") String alertType,
                               @RequestParam("gardenID") Long gardenID,
                               HttpSession session) {
        User currentUser = userService.getAuthenticatedUser();

        Optional<Garden> garden = gardenService.findGarden(gardenID);
        if (garden.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");
        }

        alertService.dismissAlert(currentUser, garden.get(), alertType);

        return "redirect:/view-garden?gardenID=" + gardenID;
    }

    private void addAttributes(User owner, Long gardenID, Model model, PlantService plantService, GardenService gardenService) {
        List<Plant> plants = plantService.getGardenPlant(gardenID);
        List<Garden> gardens = gardenService.getOwnedGardens(owner.getUserId());
        model.addAttribute("gardens", gardens);
        model.addAttribute("plants", plants);

        Optional<Garden> garden = gardenService.findGarden(gardenID);
        if (garden.isPresent()) { // if the garden ID exists
            model.addAttribute("gardenID", gardenID);
            model.addAttribute("tagInput", "");
            model.addAttribute("gardenName", garden.get().getName());
            model.addAttribute("gardenLocation", garden.get().getLocation().toString());
            model.addAttribute("gardenSize", garden.get().getSize());
            model.addAttribute("gardenTags", gardenService.getTags(gardenID));
            model.addAttribute("allTags", tagService.getTags());

            // New Code Added to get weather
            String gardenCity = garden.get().getLocation().getCity();
            String gardenCountry = garden.get().getLocation().getCountry();

            // Entered Location empty or null checks
            if (gardenCity != null && !gardenCity.isEmpty() && gardenCountry != null && !gardenCountry.isEmpty()) {
                // Location present, get weather
                // Get forecasted weather for current location
                ForecastResponse forecastResponse = weatherService.getForecastWeather(gardenCity, gardenCountry);
                // Get current weather for this location
                WeatherResponse currentWeather = weatherService.getCurrentWeather(gardenCity, gardenCountry);
                // Check if there has been rain in the past two days
                Boolean hasRained = weatherService.hasRained(gardenCity, gardenCountry);
                // Check if it is currently raining
                Boolean isRaining = weatherService.isRaining(gardenCity, gardenCountry);

                // Use the AlertService to determine if alerts should be displayed
                boolean hasNotRainedDismissed = alertService.isAlertDismissed(owner, garden.get(), "hasNotRained");
                boolean isRainingDismissed = alertService.isAlertDismissed(owner, garden.get(), "isRaining");

                // If forecastResponse is null, because API does not find weather at that location
                if (forecastResponse == null) {
                    model.addAttribute("weatherErrorMessage", "Location not found, please update your location to see the weather");
                } else {
                    // Null current weather check (for tests)
                    if (currentWeather != null) {
                        forecastResponse.addWeatherResponse(currentWeather);
                    }
                    // Add forecast weather which has the current weather added to it
                    model.addAttribute("forecastResponse", forecastResponse);

                    // Check that hasRained is successful
                    if (hasRained == null) {
                        model.addAttribute("weatherErrorMessage", "Historic weather data not available, no watering reminder available");
                    } else if (!hasRained && !hasNotRainedDismissed) {
                        // It hasn't rained in the past two days, alert hasn't been dismissed, display water plants alert
                        model.addAttribute("hasNotRainedAlert", "There hasn’t been any rain recently, make sure to water your plants if they need it");
                    } // Otherwise, it has rained in the past two days, no need to display anything

                    // Check that isRaining is successful
                    if (isRaining == null) {
                        model.addAttribute("weatherErrorMessage", "Current weather data not available, no watering reminder available");
                    } else if (isRaining && !isRainingDismissed) {
                        // It is currently raining, alert hasn't been dismissed, display water plants warning
                        model.addAttribute("isRainingAlert", "Outdoor plants don’t need any water today");
                    } // Otherwise, it is not raining, no need to display anything
                }
            } else {
                // Entered Location is empty or null
                model.addAttribute("weatherErrorMessage", "Location not found, please update your location to see the weather");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " does not exist");
        }
    }
}

