package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.ImageItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
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

        //TODO delete these 2 if statments before merging ive just updated so they dont mess with tests and I can blackbox test the story
        // DUMMY DATA
        if (badgeItems.isEmpty()) {
//            currentUser.addItem(itemService.getItemByName("Happy"));
//            currentUser.addItem(itemService.getItemByName("Eggplant"));
            badgeItems.add(itemService.getItemByName("Happy"));
            badgeItems.add(itemService.getItemByName("Eggplant"));
            userService.saveUser(currentUser);
        }
        if (imageItems.isEmpty()) {
//            currentUser.addItem(itemService.getItemByName("Cat Fall"));
//            currentUser.addItem(itemService.getItemByName("Cat Typing"));
//            currentUser.addItem(itemService.getItemByName("Fabian Intensifies"));
            imageItems.add(itemService.getItemByName("Cat Fall"));
            imageItems.add(itemService.getItemByName("Cat Typing"));
            imageItems.add(itemService.getItemByName("Fabian Intensifies"));
            userService.saveUser(currentUser);
        }

        model.addAttribute("badgeItems", badgeItems);
        model.addAttribute("imageItems", imageItems);

        return "inventoryTemplate";
    }

    @PostMapping("/inventory/use/{itemId}")
    public String useImageItem(@PathVariable Long itemId) {
        logger.info("POST /inventory/use/{}", itemId);

        // Get the current user
        User currentUser = userService.getAuthenticatedUser();

        // Get inventory
        List<Item> inventory = currentUser.getInventory();

        try {
            // Find item in inventory
            Optional<Item> matchingItem = inventory.stream()
                    .filter(item -> item.getId().equals(itemId))
                    .findFirst();

            if (matchingItem.isPresent()) {
                // Extract item, and cast to ImageItem
                ImageItem item = (ImageItem) matchingItem.get();

                // Gets item, then casts to ImageItem
                ImageItem imageItem = (ImageItem) itemService.getItemById(itemId);

                // Get the image id of imageItem
                Long itemImageId = imageItem.getImage().getId();

                // Get image from Image Table
                Optional<Image> imageOpt = imageService.findImage(itemImageId);

                // Update Users Image to ItemsImage
                imageOpt.ifPresent(currentUser::setImage);

                // Persist change to user
                userService.saveUser(currentUser);

                // Log success
                logger.info("User {} applied item {}", currentUser.getFirstName(), itemId);
            } else {
                // Item is not found
                logger.error("Item with ID {} not found", itemId);
            }

        } catch (IllegalArgumentException e) {
            logger.error("Error applying item: {}", e.getMessage());
        }

        return "redirect:/inventory";
    }
}
