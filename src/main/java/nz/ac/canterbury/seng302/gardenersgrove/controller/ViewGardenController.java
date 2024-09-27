package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.TagValidator.*;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.TipValidator.doTipValidations;

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

    private final TransactionService transactionService;

    private static final String REDIRECT_VIEW_GARDEN = "redirect:/view-garden?gardenID=";

    private static final String TIP_AMOUNT_ERROR_STR = "tipAmountError";

    private static final String TIP_INPUT_STR = "tipInput";

    private static final String TAG_EVALUATION_ERROR = "tagEvaluationError";

    private static final String WEATHER_ERROR_MESSAGE = "weatherErrorMessage";

    @Autowired
    public ViewGardenController(GardenService gardenService, PlantService plantService,
                                UserService userService, ImageService imageService,
                                TagService tagService, WeatherService weatherService,
                                ModerationService moderationService, AlertService alertService,
                                TransactionService transactionService) {
        this.gardenService = gardenService;
        this.plantService = plantService;
        this.userService = userService;
        this.imageService = imageService;
        this.tagService = tagService;
        this.weatherService = weatherService;
        this.moderationService = moderationService;
        this.alertService = alertService;
        this.transactionService = transactionService;
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

        User currentUser = userService.getAuthenticatedUser();
        Garden garden = authoriseAction(gardenID, currentUser, true);
        boolean isOwner = garden.getOwner().equals(currentUser);

        addAttributes(currentUser, gardenID, model, plantService, gardenService, session);
        session.removeAttribute(TAG_EVALUATION_ERROR);

        model.addAttribute("claimedTipsMessage", session.getAttribute("claimedTipsMessage"));
        session.removeAttribute("claimedTipsMessage");

        if (isOwner) {
            return "viewGardenDetailsTemplate";
        } else {
            return "viewUnownedGardenDetailsTemplate";
        }
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

        User currentUser = userService.getAuthenticatedUser();
        authoriseAction(gardenID, currentUser, false);

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
        return REDIRECT_VIEW_GARDEN + gardenID;
    }

    @PatchMapping("/view-garden")
    public ResponseEntity<Void> changePublicity(@RequestParam("gardenID") Long gardenID,
                                          @RequestParam("isPublic") Boolean isPublic,
                                          Model model,
                                          HttpSession session){
        logger.info("PATCH /view-garden");

        User currentUser = userService.getAuthenticatedUser();
        Garden garden = authoriseAction(gardenID, currentUser, false);

        garden.setIsPublic(isPublic);
        gardenService.addGarden(garden);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/tip-blooms")
    public String addTip(@RequestParam("gardenID") Long gardenID,
                         @RequestParam(defaultValue = "0") Integer tipAmount,
                         HttpSession session) {
        logger.info("POST /tip-blooms");

        User currentUser = userService.getAuthenticatedUser();

        doTipValidations(session, tipAmount, currentUser);

        // If there is an error, do not charge the user and return to the garden page
        if (session.getAttribute(TIP_AMOUNT_ERROR_STR) != null) {
            return REDIRECT_VIEW_GARDEN + gardenID;
        }

        // Charge the user the tip they gave the tip has already been validated.
        userService.chargeBlooms(currentUser, tipAmount);

        Garden curGarden = gardenService.findGarden(gardenID)
                .orElseThrow(() -> new NoSuchElementException("Garden not found with ID: " + gardenID));
        User owner = curGarden.getOwner();

        // Add a new transaction for the tip
        Transaction transaction = transactionService.addTransaction(tipAmount,
                "Tipped " +curGarden.getName()+ " (unclaimed by " + owner.getFirstName() + ")",
                "Garden Tip",
                owner.getUserId(),
                currentUser.getUserId(),
                false,
                curGarden);

        //Add unclaimed blooms to the garden that was tipped
        gardenService.addUnclaimedBloomTips(gardenID, tipAmount);

        return REDIRECT_VIEW_GARDEN + gardenID;
    }

    @PostMapping("/claim-tips")
    public String claimTips(@RequestParam("gardenID") Long gardenID, HttpSession session) {
        logger.info("POST /claim-tips");

        User currentUser = userService.getAuthenticatedUser();
        Garden curGarden = authoriseAction(gardenID, currentUser, false);

        List<Transaction> transactions = transactionService.retrieveGardenTips(curGarden);
        int totalUnclaimedBlooms = transactionService.totalUnclaimedTips(curGarden);

        // If no tips to claim exit
        if (transactions.isEmpty()) return REDIRECT_VIEW_GARDEN + gardenID;

        // Pay the user the total amount of unclaimed tips and remove them from the gardens unclaimed amount
        userService.addBlooms(userService.getAuthenticatedUser(), totalUnclaimedBlooms);
        gardenService.removeUnclaimedBloomTips(curGarden);

        // Set all garden's tips transactions to claimed
        transactionService.claimAllGardenTips(transactions);

        session.setAttribute("claimedTipsMessage", "You have claimed " + totalUnclaimedBlooms + " Blooms! \uD83C\uDF31");

        return REDIRECT_VIEW_GARDEN + gardenID;
    }

    @PostMapping("/add-tag")
    public String addTag(@RequestParam("gardenID") Long gardenID,
                         @RequestParam("tag") String tag,
                         Model model,
                         HttpSession session) {
        logger.info("POST /add-tag");

//        Optional<Garden> garden = gardenService.findGarden(gardenID);
        User currentUser = userService.getAuthenticatedUser();
        Garden garden = authoriseAction(gardenID, currentUser, false);

        // Get weather information
        WeatherResponse weatherResponse = weatherService.getCurrentWeather(garden.getLocation().getCity(), garden.getLocation().getCountry());
        model.addAttribute("weatherResponse", weatherResponse);
        if (tag.isEmpty()) {
            return REDIRECT_VIEW_GARDEN + gardenID;
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
                session.setAttribute(TAG_EVALUATION_ERROR, "Tag could not be evaluated at this time and will be reviewed shortly.");
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
        return REDIRECT_VIEW_GARDEN + gardenID;
    }

    @PostMapping("/dismiss-alert")
    public String dismissAlert(@RequestParam("alertType") String alertType,
                               @RequestParam("gardenID") Long gardenID,
                               HttpSession session) {

        User currentUser = userService.getAuthenticatedUser();
        Garden garden = authoriseAction(gardenID, null, false);

        alertService.dismissAlert(currentUser, garden, alertType);

        return REDIRECT_VIEW_GARDEN + gardenID;
    }

    /**
     * Checks if garden exists, then if the given user is the owner
     * @param gardenID the ID of the garden
     * @param currentUser the logged-in user
     * @return the garden if it exists
     */
    private Garden authoriseAction(Long gardenID, User currentUser, boolean onlyViewing) {
        final Optional<Garden> garden = gardenService.findGarden(gardenID);
        if (garden.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");

        if (currentUser != null) {
            final boolean isOwner = garden.get().getOwner().equals(currentUser);
            if (!isOwner) {
                if (onlyViewing) {
                    if (!garden.get().getIsPublic())
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view this garden.");
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot perform the requested action on this garden.");
                }
            }
        }

        return garden.get();
    }

    /**
     * Adds tip attributes to the model
     * @param session the session
     * @param model the model
     * @param garden the garden
     * @return the model with the tip attributes added
     */
    private Model addTipAttributes(HttpSession session, Model model, Garden garden) {
        //new code added to get Blooms tipped

        User currentUser = userService.getAuthenticatedUser();

        Integer totalBloomsTipped = garden.getTotalBloomTips();
        model.addAttribute("totalBloomsTippedMessage", "Total Blooms tipped: " + totalBloomsTipped);

        if (session.getAttribute(TIP_AMOUNT_ERROR_STR) != null) {
            model.addAttribute(TIP_AMOUNT_ERROR_STR, session.getAttribute(TIP_AMOUNT_ERROR_STR));
            model.addAttribute("showTipModal", true);
            model.addAttribute(TIP_INPUT_STR, session.getAttribute(TIP_INPUT_STR));

            session.removeAttribute(TIP_AMOUNT_ERROR_STR);
            session.removeAttribute(TIP_INPUT_STR);
        }

        boolean isOwner = garden.getOwner().equals(currentUser);
        if (isOwner) {
            Integer unclaimedBlooms = garden.getUnclaimedBlooms();
            model.addAttribute("unclaimedBlooms", unclaimedBlooms);
            model.addAttribute("claimBloomsButtonText", "Claim " + unclaimedBlooms + " Blooms");
        }
        model.addAttribute("userBloomBalance", currentUser.getBloomBalance());
        return model;
    }

    /**
     * Adds attributes to the model
     * @param owner the owner of the garden
     * @param gardenID
     * @param model the model
     * @param plantService the plant service
     * @param gardenService the garden service
     * @param session the session
     */
    private void addAttributes(User owner, Long gardenID, Model model, PlantService plantService,
                               GardenService gardenService, HttpSession session) {
        List<Plant> plants = plantService.getGardenPlant(gardenID);
        List<Garden> gardens = gardenService.getOwnedGardens(owner.getUserId());
        model.addAttribute("gardens", gardens);
        model.addAttribute("plants", plants);

        Optional<Garden> garden = gardenService.findGarden(gardenID);
        if (garden.isPresent()) {
            addGardenAttributes(garden.get(), gardenID, model, session);
            addWeatherAttributes(owner, garden.get(), model);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " does not exist");
        }
    }

    private void addGardenAttributes(Garden garden, Long gardenID, Model model, HttpSession session) {
        model.addAttribute("gardenID", gardenID);
        model.addAttribute("tagInput", "");
        model.addAttribute("gardenName", garden.getName());
        model.addAttribute("gardenLocation", garden.getLocation().toString());
        model.addAttribute("gardenDescription", garden.getDescription());
        model.addAttribute("gardenSize", garden.getSize());
        model.addAttribute("gardenTags", gardenService.getEvaluatedTags(gardenID));
        model.addAttribute("gardenIsPublic", garden.getIsPublic());
        model.addAttribute("allTags", tagService.getTagsByEvaluated(true));
        model.addAttribute("tagError", session.getAttribute(TAG_EVALUATION_ERROR));

        addTipAttributes(session, model, garden);
    }

    /**
     * Adds weather attributes to the model
     * @param owner the owner of the garden
     * @param garden the garden
     * @param model the model
     */
    private void addWeatherAttributes(User owner, Garden garden, Model model) {
        String gardenCity = garden.getLocation().getCity();
        String gardenCountry = garden.getLocation().getCountry();

        if (isValidLocation(gardenCity, gardenCountry)) {
            ForecastResponse forecastResponse = weatherService.getForecastWeather(gardenCity, gardenCountry);
            WeatherResponse currentWeather = weatherService.getCurrentWeather(gardenCity, gardenCountry);
            Boolean hasRained = weatherService.hasRained(gardenCity, gardenCountry);
            Boolean isRaining = weatherService.isRaining(gardenCity, gardenCountry);
            handleWeatherAlerts(owner, garden, model, forecastResponse, currentWeather, hasRained, isRaining);
        } else {
            model.addAttribute(WEATHER_ERROR_MESSAGE, "Location not found, please update your location to see the weather");
        }
    }

    /**
     * Checks if the location is valid
     * @param city the city
     * @param country the country
     * @return true if the location is valid, false otherwise
     */
    private boolean isValidLocation(String city, String country) {
        return city != null && !city.isEmpty() && country != null && !country.isEmpty();
    }

    private void handleWeatherAlerts(User owner, Garden garden, Model model,
                                     ForecastResponse forecastResponse, WeatherResponse currentWeather,
                                     Boolean hasRained, Boolean isRaining) {
        User currentUser = userService.getAuthenticatedUser();
        User gardenOwner = garden.getOwner();

        if (forecastResponse == null) {
            String errorMessage = currentUser.equals(gardenOwner)
                    ? "Location not found, please update your location to see the weather"
                    : "Location not found, please contact the garden owner for more information";
            model.addAttribute(WEATHER_ERROR_MESSAGE, errorMessage);
        } else {
            if (currentWeather != null) {
                forecastResponse.addWeatherResponse(currentWeather);
            }
            model.addAttribute("forecastResponse", forecastResponse);
            addRainAlerts(owner, garden, model, hasRained, isRaining);
        }
    }

    /**
     * Adds rain alerts to the model
     * @param owner the owner of the garden
     * @param garden the garden
     * @param model the model
     * @param hasRained whether it has rained
     * @param isRaining whether it is raining
     */
    private void addRainAlerts(User owner, Garden garden, Model model, Boolean hasRained, Boolean isRaining) {
        boolean hasNotRainedDismissed = alertService.isAlertDismissed(owner, garden, "hasNotRained");
        boolean isRainingDismissed = alertService.isAlertDismissed(owner, garden, "isRaining");

        if (hasRained == null) {
            model.addAttribute(WEATHER_ERROR_MESSAGE, "Historic weather data not available, no watering reminder available");
        } else if (!hasRained && !hasNotRainedDismissed) {
            model.addAttribute("hasNotRainedAlert", "There hasn’t been any rain recently, make sure to water your plants if they need it");
        }

        if (isRaining == null) {
            model.addAttribute(WEATHER_ERROR_MESSAGE, "Current weather data not available, no watering reminder available");
        } else if (isRaining && !isRainingDismissed) {
            model.addAttribute("isRainingAlert", "Outdoor plants don’t need any water today");
        }
    }

}

