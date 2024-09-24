package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.ItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class InventoryController {
    Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public InventoryController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @GetMapping("/inventory")
    public String getTemplate(Model model) {

        User currentUser = userService.getAuthenticatedUser();

        logger.info("GET /inventory");
        RedirectService.addEndpoint("/inventory");

        //Get the current user
        User currentUser = userService.getAuthenticatedUser();

        //Create and populate a list of items for the view to render
//        List<String[]> badgeItems = new ArrayList<>();
        List<Item> badgeItems = itemService.getBadgesByOwner(currentUser.getUserId());

        // TODO - Simulating the adding of items, this will be done using service and repo layers in another task
        badgeItems.add(new String[]{"1x", "vegemite.png", "Vegemite"});
        badgeItems.add(new String[]{"1x", "timtam.png", "Tim Tam"});
        badgeItems.add(new String[]{"1x", "neo_fabian.png", "Neo Fabian"});

        //Create and populate a list of items for the view to render
//        List<String[]> gifItems = new ArrayList<>();
        List<Item> imageItems = itemService.getImagesByOwner(currentUser.getUserId());

        // TODO - Simulating the adding of items, this will be done using service and repo layers in another task
        gifItems.add(new String[]{"1x", "fabian.gif", "Fabian Intensifies"});
        gifItems.add(new String[]{"1x", "scrum_master_harrison.gif", "Scrum Master Harrison"});
        gifItems.add(new String[]{"1x", "stick_man.gif", "Stick Man"});

        model.addAttribute("user", currentUser);
        if (badgeItems.isEmpty()) {
            currentUser.addItem(itemService.getItemByName("Happy"));
            currentUser.addItem(itemService.getItemByName("Eggplant"));
            userService.addUser(currentUser);
        }
        if (imageItems.isEmpty()) {
            currentUser.addItem(itemService.getItemByName("Cat Fall"));
            currentUser.addItem(itemService.getItemByName("Cat Typing"));
            currentUser.addItem(itemService.getItemByName("Fabian Intensifies"));
            userService.addUser(currentUser);
        }

        model.addAttribute("badgeItems", badgeItems);
        model.addAttribute("imageItems", imageItems);

        return "inventoryTemplate";
    }

    @PostMapping("/inventory/updateBadgeURL")
    public String updateUserBadge(@RequestParam Long userId, @RequestParam String badgeURL) {
        User user = userService.getUserByID(userId);
        user.setBadgeURL(badgeURL);
        userService.saveUser(user);
        return "redirect:/inventory";
    }

    @PostMapping("/inventory/updateProfile")
    public String updateUserProfile(@RequestParam Long userId, @RequestParam String imageURL) {
        User user = userService.getUserByID(userId);

        user.setImageURL(imageURL);
        userService.saveUser(user);
        return "redirect:/inventory";
    }




}
