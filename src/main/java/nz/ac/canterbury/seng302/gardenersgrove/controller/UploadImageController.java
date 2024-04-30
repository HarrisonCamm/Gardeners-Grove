package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This controller handles the MVC part of the upload image page and also gets an image for a plant
 */
@Controller
public class UploadImageController {

    Logger logger = LoggerFactory.getLogger(UploadImageController.class);

    private final PlantService plantService;
    private final UserService userService;

    @Autowired
    public UploadImageController(PlantService plantService, UserService userService) {
        this.plantService = plantService;
        this.userService = userService;
    }

    /**
     * This method sets up the upload image page
     * @param viewGarden boolean to check if the user is viewing a garden
     * @param editPlant boolean to check if the user is editing a plant
     * @param plantID  The ID of the plant to upload an image for
     * @param gardenID The ID of the garden the plant is in
     * @param model Spring Boot model
     * @param userID The ID of the user to upload an image for
     * @return HTML ThymeLeaf template file name
     */
    @GetMapping("/upload-image")
    public String uploadImage(@RequestParam(value = "view-garden", required = false) boolean viewGarden,
                              @RequestParam(value = "edit-plant", required = false) boolean editPlant,
                              @RequestParam(value = "create-plant-picture", required = false) boolean createPlant,
                              @RequestParam(value = "view-user-profile", required = false) boolean viewUser,
                              @RequestParam(value = "edit-user-profile-image", required = false) boolean editUserProfile,
                              @RequestParam(value = "plantID", required = false) Long plantID,
                              @RequestParam(value = "gardenID", required = false) Long gardenID,
                              @RequestParam(value = "userID", required = false) Long userID,
                              Model model) {

        logger.info("GET /upload-image");

        model.addAttribute("lastEndpoint", RedirectService.getPreviousPage());

        if (viewGarden) {
            RedirectService.addEndpoint("/view-garden?gardenID=" + gardenID);
        } else if (editPlant) {
            RedirectService.addEndpoint("/edit-plant?plantID=" + plantID);
        } else if (createPlant) {
            RedirectService.addEndpoint("/create-plant?gardenID=" + gardenID);
        } else if (viewUser) {
            RedirectService.addEndpoint("/view-user-profile");
        } else if (editUserProfile) {
            RedirectService.addEndpoint("/edit-user-profile");
        }

        return "uploadImageTemplate";
    }


    /**
     * This method gets the image for a plant
     * @param viewGarden boolean to check if the user is viewing a garden
     * @param editPlant boolean to check if the user is editing a plant
     * @param gardenID The ID of the garden the plant is in
     * @param plantID The ID of the plant to get the image for
     * @return The image for the plant
     */
    // Generated by GitHub Copilot
    @GetMapping("/get-image")
    public ResponseEntity<byte[]> getPlantImage(@RequestParam(value = "view-garden", required = false) boolean viewGarden,
                                                @RequestParam(value = "edit-plant", required = false) boolean editPlant,
                                                @RequestParam(value = "view-user-profile", required = false) boolean viewUser,
                                                @RequestParam(value = "edit-user-profile-image", required = false) boolean editUserProfile,
                                                @RequestParam(value = "gardenID", required = false) Long gardenID,
                                                @RequestParam(value = "userID", required = false) Long userID,
                                                @RequestParam(value = "plantID", required = false) Long plantID,
                                                Model model) {

        byte[] image;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        //Add cases for required image (plant or user)
        if (!viewUser && !editUserProfile) {
            Plant plant = plantService.findPlant(plantID).get();
            image = plant.getImage();
            model.addAttribute("id", plantID);
            model.addAttribute("picture", image);
        } else {
            User user = userService.getUserByID(userID);
            image = user.getImage();
            model.addAttribute("id", userID);
            model.addAttribute("picture", image);
        }


        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
