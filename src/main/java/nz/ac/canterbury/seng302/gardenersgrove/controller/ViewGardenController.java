package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ViewGardenController {

    private final ImageService imageService;
    Logger logger = LoggerFactory.getLogger(ViewGardenController.class);

    private final GardenService gardenService;
    private final PlantService plantService;
    private final UserService userService;
    private final TagService tagService;

    public ViewGardenController(GardenService gardenService, PlantService plantService,
                                UserService userService, ImageService imageService,
                                TagService tagService) {
        this.gardenService = gardenService;
        this.plantService  = plantService;
        this.userService = userService;
        this.imageService = imageService;
        this.tagService = tagService;
    }

    @GetMapping("/view-garden")
    public String viewGarden(@RequestParam("gardenID") Long gardenID,
                             @RequestParam(value = "tag", required = false) String tag,
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
        User currentUser = userService.getAuthenicatedUser();
        if (garden.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");
        else if (!garden.get().getOwner().equals(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view this garden.");

        return addAttributes(currentUser, gardenID, tag, model, plantService, gardenService);
    }

    /**
     *
     * @param plantID The ID of the plant to add the picture to
     * @param gardenID The ID of the garden the plant is in
     * @param file The image file to upload
     * @param model The Spring Boot model
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
        User currentUser = userService.getAuthenicatedUser();
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

        addAttributes(currentUser, gardenID, "", model, plantService, gardenService);

        return "redirect:/view-garden?gardenID=" +gardenID;
    }

    @PostMapping("/add-tag")
    public String addTag(@RequestParam("gardenID") Long gardenID,
                         @RequestParam("tag") String tag,
                         Model model) {
        logger.info("POST /add-tag");

        Optional<Garden> garden = gardenService.findGarden(gardenID);
        User currentUser = userService.getAuthenicatedUser();
        if (garden.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " not present");
        else if (!garden.get().getOwner().equals(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view this garden.");

        // Add tag to the database
        tagService.addTag(new Tag(garden.get().getId(), tag));

        return "redirect:/view-garden?gardenID=" + gardenID;
    }

    private String addAttributes(User owner, @RequestParam("gardenID") Long gardenID, @RequestParam("tag") String tag, Model model, PlantService plantService, GardenService gardenService) {
        List<Plant> plants = plantService.getGardenPlant(gardenID);
        List<Garden> gardens = gardenService.getOwnedGardens(owner.getUserId());
        model.addAttribute("gardens", gardens);
        model.addAttribute("plants", plants);

        Optional<Garden> garden = gardenService.findGarden(gardenID);
        if (garden.isPresent()) { // if the garden ID exists
            model.addAttribute("gardenID", gardenID);
            model.addAttribute("tagInput", tag);
            model.addAttribute("gardenName", garden.get().getName());
            model.addAttribute("gardenLocation", garden.get().getLocation().toString());
            model.addAttribute("gardenSize", garden.get().getSize());
            model.addAttribute("gardenTags", tagService.getGardenTags(gardenID));
            model.addAttribute("allTags", tagService.getTags());
            return "viewGardenDetailsTemplate";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Garden with ID " + gardenID + " does not exist");
        }
    }
}
