package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ViewGardensController {

    Logger logger = LoggerFactory.getLogger(ViewGardensController.class);

    private final GardenService gardenService;
    private final UserService userService;

    @Autowired
    public ViewGardensController(GardenService gardenService, UserService userService) {
        this.gardenService = gardenService;
        this.userService = userService;
    }

    @GetMapping("/view-gardens")
    public String view(Model model,
                       HttpServletRequest req,
                       HttpSession session) {
        logger.info("GET /view-gardens");
        RedirectService.addEndpoint("/view-gardens");

        User currentUser = userService.getAuthenicatedUser();

        List<Garden> gardens = gardenService.getOwnedGardens(currentUser.getUserId());
        model.addAttribute("gardens", gardens);
        return "viewGardensTemplate";
    }
}
