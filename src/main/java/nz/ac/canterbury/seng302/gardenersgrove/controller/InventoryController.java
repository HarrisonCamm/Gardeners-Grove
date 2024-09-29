package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Controller
public class InventoryController {
    Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private final ItemService itemService;
    private final UserService userService;
    private final InventoryService inventoryService;
    private final ImageService imageService;

    @Autowired
    public InventoryController(ItemService itemService, UserService userService, InventoryService inventoryService, ImageService imageService) {
        this.itemService = itemService;
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.imageService = imageService;
    }

    @GetMapping("/inventory")
    public String getTemplate(Model model) {
        logger.info("GET /inventory");
        RedirectService.addEndpoint("/inventory");

        // Get the current user
        User currentUser = userService.getAuthenticatedUser();
        List<Inventory> items = inventoryService.getUserInventory(currentUser);

        List<Map.Entry<Item,Integer>> ownedItems= new ArrayList<>();

        for (Inventory inventory: items) {
            Item item = inventory.getItem();
            Integer quantity = inventory.getQuantity();
            Map.Entry<Item,Integer> itemEntry = new AbstractMap.SimpleEntry<>(item, quantity);
            ownedItems.add(itemEntry);
        }

        List<Map.Entry<Item,Integer>> badgeItems = new ArrayList<>();
        List<Map.Entry<Item,Integer>> imageItems = new ArrayList<>();

        for (Map.Entry<Item,Integer> item: ownedItems) {
            if (item.getKey() instanceof BadgeItem) {
                badgeItems.add(item);
            }
            if (item.getKey() instanceof ImageItem) {
                imageItems.add(item);
            }
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
