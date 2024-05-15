package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
    private final ImageService imageService;

    @Autowired
    public UploadImageController(PlantService plantService, UserService userService, ImageService imageService) {
        this.plantService = plantService;
        this.userService = userService;
        this.imageService = imageService;
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
     * Uploads a temporary image
     * @param file The image file to upload
     * @param model Spring Boot model
     * @return The ID of the image
     * @throws IOException If the file cannot be read
     */
    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadTemporaryImage(@RequestParam("file") MultipartFile file,
                                                       HttpSession session,
                                                       Model model) throws IOException {
        Image image = new Image(file, true);
        image = imageService.saveImage(image);
        Image.setTemporaryImage(session, image);
        return new ResponseEntity<>(image.getId().toString(), new HttpHeaders(), HttpStatus.CREATED);
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
                                                @RequestParam(value = "temporary", required = false) boolean temporary,
                                                @RequestParam(value = "gardenID", required = false) Long gardenID,
                                                @RequestParam(value = "userID", required = false) Long userID,
                                                @RequestParam(value = "plantID", required = false) Long plantID,
                                                @RequestParam(value = "imageID", required = false) Long imageID,
                                                Model model) {

        Image image;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        //Add cases for required image (plant or user)
        if (temporary) {
            image = imageService.findImage(imageID).get();
            if (image.getExpiryDate() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image is not temporary");
            }
            model.addAttribute("id", imageID);
            model.addAttribute("picture", image.getData());
        } else if (!viewUser && !editUserProfile) {
            Plant plant = plantService.findPlant(plantID).get();
            image = plant.getImage();
            model.addAttribute("id", plantID);
            model.addAttribute("picture", image.getData());
        } else {
            User user = userService.getUserByID(userID);
            image = user.getImage();
            model.addAttribute("id", userID);
            model.addAttribute("picture", image.getData());
        }

        return new ResponseEntity<>(image.getData(), headers, HttpStatus.OK);
    }
}
