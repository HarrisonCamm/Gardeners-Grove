package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class InventoryController {
    Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private final ItemService itemService;
    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public InventoryController(ItemService itemService, UserService userService, ImageService imageService) {
        this.itemService = itemService;
        this.userService = userService;
        this.imageService = imageService;
    }

//    @GetMapping("/inventory")
//    public String getTemplate(Model model) {
//        logger.info("GET /inventory");
//        RedirectService.addEndpoint("/inventory");
//
//        // Get the current user
//        User currentUser = userService.getAuthenticatedUser();
//        List<Item> badgeItems = itemService.getBadgesByOwner(currentUser.getUserId());
//
//        // TODO - Simulating the adding of items, this will be done using service and repo layers in another task
//        // uncomment these to view how it would look, commented out for tests to work
////        badgeItems.add(new String[]{"1x", "vegemite.png", "Vegemite"});
////        badgeItems.add(new String[]{"1x", "timtam.png", "Tim Tam"});
////        badgeItems.add(new String[]{"1x", "neo_fabian.png", "Neo Fabian"});
//
//        //Create and populate a list of items for the view to render
////        List<String[]> gifItems = new ArrayList<>();
//        List<Item> imageItems = itemService.getImagesByOwner(currentUser.getUserId());
//
//        // TODO - Simulating the adding of items, this will be done using service and repo layers in another task
////        gifItems.add(new String[]{"1x", "fabian.gif", "Fabian Intensifies"});
////        gifItems.add(new String[]{"1x", "scrum_master_harrison.gif", "Scrum Master Harrison"});
////        gifItems.add(new String[]{"1x", "stick_man.gif", "Stick Man"});
//
//        if (badgeItems.isEmpty()) {
//            currentUser.addItem(itemService.getItemByName("Happy"));
//            currentUser.addItem(itemService.getItemByName("Eggplant"));
//            userService.addUser(currentUser);
//        }
//        if (imageItems.isEmpty()) {
//            currentUser.addItem(itemService.getItemByName("Cat Fall"));
//            currentUser.addItem(itemService.getItemByName("Cat Typing"));
//            currentUser.addItem(itemService.getItemByName("Fabian Intensifies"));
//            userService.addUser(currentUser);
//        }
//
//        model.addAttribute("badgeItems", badgeItems);
//        model.addAttribute("imageItems", imageItems);
//
//        return "inventoryTemplate";
//    }


    @GetMapping("/inventory")
    public String getTemplate(Model model) {
        logger.info("GET /inventory");
        RedirectService.addEndpoint("/inventory");

        // Get the current user
        User currentUser = userService.getAuthenticatedUser();
        List<Item> badgeItems = itemService.getBadgesByOwner(currentUser.getUserId());
        List<Item> imageItems = itemService.getImagesByOwner(currentUser.getUserId());

        // DUMMY DATA
        if (badgeItems.isEmpty()) {
            currentUser.addItem(itemService.getItemByName("Tim Tam"));
            currentUser.addItem(itemService.getItemByName("Vegemite"));
            currentUser.addItem(itemService.getItemByName("Love"));
            userService.saveUser(currentUser);
        }
        if (imageItems.isEmpty()) {
            currentUser.addItem(itemService.getItemByName("Cat Fall"));
            currentUser.addItem(itemService.getItemByName("Cat Typing"));
            currentUser.addItem(itemService.getItemByName("Fabian Intensifies"));
            userService.saveUser(currentUser);
        }

        model.addAttribute("badgeItems", badgeItems);
        model.addAttribute("imageItems", imageItems);
        model.addAttribute("user", currentUser);

        return "inventoryTemplate";
    }









//    @PostMapping("/inventory/updateBadgeURL")
//    public String updateUserBadge(@RequestParam Long userId, @RequestParam String badgeURL) {
//        User user = userService.getUserByID(userId);
//        user.setBadgeURL(badgeURL);
//        userService.saveUser(user);
//        return "redirect:/inventory";
//    }


    @PostMapping("/inventory/updateBadge")
    public String updateUserBadge(@RequestParam Long userId, @RequestParam Long badgeId) {
        User user = userService.getUserByID(userId);
//        user.setBadgeURL(badgeURL);
        userService.saveUser(user);
        return "redirect:/inventory";
    }


    @PostMapping("/inventory/use/{itemId}")
    public String useImageItem(@PathVariable Long itemId) {
        logger.info("POST /inventory/use/{}", itemId);

        // Get the current user
        User currentUser = userService.getAuthenticatedUser();

        try {
            // Gets item, then casts to ImageItem
            BadgeItem badgeItem = (BadgeItem) itemService.getItemById(itemId);

            // Get the image id of imageItem
            Long itemImageId = badgeItem.getIcon().getId();

            // Update Users Badge
            currentUser.setAppliedBadge(badgeItem);

            // Persis change to user
            userService.saveUser(currentUser);

            logger.info("User {} applied item {}", currentUser.getFirstName(), itemId);
        } catch (IllegalArgumentException e) {
            logger.error("Error applying item: {}", e.getMessage());
        }

        return "redirect:/inventory";
    }

}
