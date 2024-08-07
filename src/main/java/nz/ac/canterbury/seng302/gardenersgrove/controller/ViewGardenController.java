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
import static nz.ac.canterbury.seng302.gardenersgrove.validation.TagValidator.*;

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

    @Autowired
    public ViewGardenController(GardenService gardenService, PlantService plantService,
                                UserService userService, ImageService imageService,
                                TagService tagService, WeatherService weatherService,
                                ModerationService moderationService) {
        this.gardenService = gardenService;
        this.plantService = plantService;
        this.userService = userService;
        this.imageService = imageService;
        this.tagService = tagService;
        this.weatherService = weatherService;
        this.moderationService = moderationService;
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

        addAttributes(currentUser, gardenID, model, plantService, gardenService, session);
        session.removeAttribute("tagEvaluationError");
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

        addAttributes(currentUser, gardenID, model, plantService, gardenService, session);
        return "redirect:/view-garden?gardenID=" + gardenID;
    }

    @PostMapping("/add-tag")
    public String addTag(@RequestParam("gardenID") Long gardenID,
                         @RequestParam("tag") String tag,
                         Model model,
                         HttpSession session) {
        logger.info("POST /add-tag");

        Optional<Garden> garden = gardenService.findGarden(gardenID);
        User currentUser = userService.getAuthenticatedUser();
        if (garden.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");
        else if (!garden.get().getOwner().equals(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view this garden.");

        // Get weather information
        WeatherResponse weatherResponse = weatherService.getCurrentWeather(garden.get().getLocation().getCity(), garden.get().getLocation().getCountry());
        model.addAttribute("weatherResponse", weatherResponse);
        if (tag.isEmpty()) {
            return "redirect:/view-garden?gardenID=" + gardenID;
        }

        // Check if this is a duplicate tag before moderation
        List<Tag> allTags = tagService.getTags();
        Tag addedTag;
        boolean tagExists = allTags.stream().anyMatch(existingTag -> existingTag.getName().equals(tag));
        if (tagExists) {
            addedTag = tagService.getTagByName(tag);
        } else {
            doTagValidations(model, tag);
            if (model.containsAttribute("tagTextError") || model.containsAttribute("tagLengthError")) {
                addAttributes(currentUser, gardenID, model, plantService, gardenService, session);
                return "viewGardenDetailsTemplate";
            }

            // Add tag to the database
            addedTag = new Tag(tag, false);

            // Moderate the tag before adding
            String possibleTerms = moderationService.moderateText(tag);

            if (possibleTerms.equals("evaluation_error")) {
                // Add tag to a waiting list for later evaluation

                // Show evaluation error
                logger.info("help");
                session.setAttribute("tagEvaluationError", "Tag could not be evaluated at this time and will be reviewed shortly.");
            } else  {
                if (!isAppropriateName(possibleTerms)) {
                    model.addAttribute("profanityTagError", "Profanity or inappropriate language detected");
                    addAttributes(currentUser, gardenID, model, plantService, gardenService, session);
                    return "viewGardenDetailsTemplate";
                }

                addedTag.setEvaluated(true);
            }

            // Add tag to database
            tagService.addTag(addedTag);
        }

        List<Tag> gardenTags = gardenService.getTags(gardenID);
        Tag finalAddedTag = addedTag;
        if (!gardenTags.stream().anyMatch(existingTag -> existingTag.equals(finalAddedTag))) {
            // add the tag to the garden's list of tags
            gardenService.addTagToGarden(gardenID, addedTag);
        } else {
            model.addAttribute("duplicateTagError", "Tag is already defined");
            addAttributes(currentUser, gardenID, model, plantService, gardenService, session);
            return "viewGardenDetailsTemplate";
        }

        // Return user to page
        return "redirect:/view-garden?gardenID=" + gardenID;

    }

    private void addAttributes(User owner, Long gardenID, Model model, PlantService plantService, GardenService gardenService, HttpSession session) {
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
            model.addAttribute("gardenTags", gardenService.getEvaluatedTags(gardenID));
            model.addAttribute("allTags", tagService.getTagsByEvaluated(true));
            model.addAttribute("tagError", session.getAttribute("tagEvaluationError"));


            //New Code Added to get weather
            String gardenCity = garden.get().getLocation().getCity();
            String gardenCountry = garden.get().getLocation().getCountry();
            ForecastResponse forecastResponse = weatherService.getForecastWeather(gardenCity, gardenCountry);       //Get forecast
            WeatherResponse currentWeather = weatherService.getCurrentWeather(gardenCity, gardenCountry);       //Get current weather
            if (currentWeather != null) forecastResponse.addWeatherResponse(currentWeather);                    //Add current weather to forecast
            model.addAttribute("forecastResponse", forecastResponse);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " does not exist");
        }
    }
}
