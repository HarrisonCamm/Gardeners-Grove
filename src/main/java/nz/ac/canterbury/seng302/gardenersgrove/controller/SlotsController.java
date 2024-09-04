package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.SlotsService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.List;

@Controller
public class SlotsController {

    int SPIN_COST = 50;

    Logger logger = LoggerFactory.getLogger(SlotsController.class);
    private final UserService userService;

    @Autowired
    public SlotsController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets the thymeleaf page representing the /daily-spin page
     * @param model (map-like) representation of data to be used in thymeleaf display
     * @return thymeleaf demoTemplate
     */
    @GetMapping("/daily-spin")
    public String getTemplate(Model model) {
        logger.info("GET /daily-spin");

        User user = userService.getAuthenticatedUser();

        //Slots logic 💧☀️🍄🌶️🌾
        List<int[]> slots = SlotsService.generateSlots();
        int amountWon = SlotsService.amountWon(slots);

        model.addAttribute("slots", slots);
        model.addAttribute("amountWon", amountWon);
        model.addAttribute("bloomBalance", user.getBloomBalance());
        //TODO in other task: pass counter or boolean if the user has spinned before

        userService.addBlooms(user, amountWon);
        userService.chargeBlooms(user, SPIN_COST);


        return "dailySpinTemplate";
    }
}
