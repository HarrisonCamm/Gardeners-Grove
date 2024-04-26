package nz.ac.canterbury.seng302.gardenersgrove.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UploadImageController {

    Logger logger = LoggerFactory.getLogger(UploadImageController.class);

    @GetMapping("/upload-image")
    public String uploadImage(@RequestParam("view-garden") boolean viewGarden,
                              @RequestParam("plantID") Long plantID) {

        logger.info("GET /upload-image");

        return "uploadImageTemplate";
    }
}
