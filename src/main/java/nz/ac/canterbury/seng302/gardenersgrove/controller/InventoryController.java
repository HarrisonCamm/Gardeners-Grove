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

    @GetMapping("/inventory")
    public String getTemplate(Model model) {
        logger.info("GET /inventory");
        RedirectService.addEndpoint("/inventory");

        // Get the current user
        User currentUser = userService.getAuthenticatedUser();
        List<Item> badgeItems = itemService.getBadgesByOwner(currentUser.getUserId());
        List<Item> imageItems = itemService.getImagesByOwner(currentUser.getUserId());

//        // DUMMY DATA
//        if (badgeItems.isEmpty()) {
//            currentUser.addItem(itemService.getItemByName("Tim Tam"));
//            currentUser.addItem(itemService.getItemByName("Vegemite"));
//            currentUser.addItem(itemService.getItemByName("Love"));
//            userService.saveUser(currentUser);
//        }
//        if (imageItems.isEmpty()) {
//            currentUser.addItem(itemService.getItemByName("Cat Fall"));
//            currentUser.addItem(itemService.getItemByName("Cat Typing"));
//            currentUser.addItem(itemService.getItemByName("Fabian Intensifies"));
//            userService.saveUser(currentUser);
//        }

        model.addAttribute("badgeItems", badgeItems);
        model.addAttribute("imageItems", imageItems);
        model.addAttribute("user", currentUser);

        return "inventoryTemplate";
    }
    
    @PostMapping("/inventory/badge/use/{itemId}")
    public String useBadgeItem(@PathVariable Long itemId) {
        logger.info("POST /inventory/use/{}", itemId);

        // Get the current user
        User currentUser = userService.getAuthenticatedUser();

        try {
            // Gets item, then casts to ImageItem
            BadgeItem badgeItem = (BadgeItem) itemService.getItemById(itemId);

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

    @PostMapping("/inventory/gif/use/{itemId}")
    public String useGifItem(@PathVariable Long itemId) {
        logger.info("POST /inventory/use/{}", itemId);

        // Get the current user
        User currentUser = userService.getAuthenticatedUser();

        try {
            // Gets item, then casts to ImageItem
            ImageItem imageItem = (ImageItem) itemService.getItemById(itemId);

            // Get the image id of imageItem
            Long itemImageId = imageItem.getImage().getId();

            // Get image from Image Table
            Optional<Image> image = imageService.findImage(itemImageId);

            // Check if the current user's image does not equal the item image ID
            if (!currentUser.getImage().getId().equals(itemImageId)) {
                // Store the current profile image ID for the ability for user to revert back
                currentUser.setPreviousImageId(currentUser.getImage().getId());
            }

            // Update Users Image to ItemsImage
            image.ifPresent(currentUser::setImage);

            // Persis change to user
            userService.saveUser(currentUser);

            logger.info("User {} applied item {}", currentUser.getFirstName(), itemId);
        } catch (IllegalArgumentException e) {
            logger.error("Error applying item: {}", e.getMessage());
        }

        return "redirect:/inventory";
    }



}
