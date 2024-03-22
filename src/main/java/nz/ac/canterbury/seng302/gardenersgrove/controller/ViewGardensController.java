package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewGardensController {

    Logger logger = LoggerFactory.getLogger(ViewGardensController.class);

    private final GardenService gardenService;

    @Autowired
    public ViewGardensController(GardenService gardenService) { this.gardenService = gardenService; }

    @GetMapping("/View Gardens")
    public String view(Model model,
                       HttpServletRequest req,
                       HttpSession session) {
        logger.info("GET /View Gardens");
        RedirectService.addEndpoint("/View Gardens");

        model.addAttribute("gardens", gardenService.getGardens());
        return "viewGardensTemplate";
    }
}
