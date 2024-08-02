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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

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
    public String view(@RequestParam(name="id", required = false) Long friendId,
                       Model model,
                       HttpServletRequest req,
                       HttpSession session) {
        logger.info("GET /view-gardens");
        RedirectService.addEndpoint("/view-gardens");

        User currentUser = userService.getAuthenicatedUser();

        if (friendId != null) {
            boolean isFriend = currentUser.getFriends().stream()
                    .anyMatch(friend -> friend.getUserId().equals(friendId));
            if (!isFriend) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view gardens of a user who is not your friend.");
            }
        }
        final Long userId = friendId != null ? friendId : currentUser.getUserId();

        List<Garden> gardens = gardenService.getOwnedGardens(userId);
        List<Garden> myGardens = gardenService.getOwnedGardens(currentUser.getUserId());
        model.addAttribute("gardens", gardens);
        model.addAttribute("myGardens", myGardens);
        return "viewGardensTemplate";
    }
}
