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
    private final InventoryItemService inventoryService;
    private final ImageService imageService;

    @Autowired
    public InventoryController(ItemService itemService, UserService userService, InventoryItemService inventoryService, ImageService imageService) {
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
        List<InventoryItem> items = inventoryService.getUserInventory(currentUser);

        List<Map.Entry<Item,Integer>> ownedItems = new ArrayList<>();

        for (InventoryItem inventory : items) {
            Item item = inventory.getItem();
            Integer quantity = inventory.getQuantity();
            Map.Entry<Item,Integer> itemEntry = new AbstractMap.SimpleEntry<>(item, quantity);
            ownedItems.add(itemEntry);
        }

        List<Map.Entry<Item,Integer>> badgeItems = new ArrayList<>();
        List<Map.Entry<Item,Integer>> imageItems = new ArrayList<>();

        for (Map.Entry<Item,Integer> item : ownedItems) {
            if (item.getKey() instanceof BadgeItem) {
                badgeItems.add(item);
            }
            if (item.getKey() instanceof ImageItem) {
                imageItems.add(item);
            }
        }

        if (currentUser.getImage() != null && !currentUser.getUploadedImageId().equals(currentUser.getImage().getId())) {
            model.addAttribute("unapplyItemId", inventoryService.getInventoryByOwnerIdAndImageId(currentUser.getUserId(), currentUser.getImage().getId()).getItem().getId());
        }

        if (currentUser.getAppliedBadge() != null) {
            model.addAttribute("unapplyBadgeId", currentUser.getAppliedBadge().getId());
        }

        model.addAttribute("badgeItems", badgeItems);
        model.addAttribute("imageItems", imageItems);
        model.addAttribute("user", currentUser);

        return "inventoryTemplate";
    }

    @PostMapping("/inventory/badge/use/{itemId}")
    public String useBadgeItem(@PathVariable Long itemId) {
        logger.info("POST /inventory/badge/use/{}", itemId);

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
        logger.info("POST /inventory/gif/use/{}", itemId);

        // Get the current user
        User currentUser = userService.getAuthenticatedUser();

        // Get inventory
        List<InventoryItem> inventory = inventoryService.getUserInventory(currentUser);

        try {
            // Find item in inventory
            Optional<InventoryItem> matchingItemInInventory = inventory.stream()
                    .filter(item -> item.getItem().getId().equals(itemId))
                    .findFirst();
            if (matchingItemInInventory.isPresent()) {
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

    /**
     * Unapply an inventory image item from the user's profile
     *
     * @param itemId The ID of the item to unapply
     * @return Redirect to the inventory page
     */
    @PostMapping("/inventory/gif/unapply/{itemId}")
    public String unapplyImageItem(@PathVariable Long itemId) {
        logger.info("POST /inventory/gif/unapply/{}", itemId);

        // Get the current user
        User currentUser = userService.getAuthenticatedUser();
        InventoryItem unapplyItem = inventoryService.getInventoryByOwnerIdAndImageId(currentUser.getUserId(), currentUser.getImage().getId());

        if (unapplyItem.getItem().getId().equals(itemId)) {
            Optional<Image> imageOpt = imageService.findImage(currentUser.getUploadedImageId());
            if (imageOpt.isPresent()) {
                currentUser.setImage(imageOpt.get());
                userService.saveUser(currentUser);
            }
        }

        return "redirect:/inventory";
    }

    /**
     * Unapply an inventory badge item from the user's name
     *
     * @param itemId The ID of the item to unapply
     * @return Redirect to the inventory page
     */
    @PostMapping("/inventory/badge/unapply/{itemId}")
    public String unapplyBadgeItem(@PathVariable Long itemId) {
        logger.info("POST /inventory/badge/unapply/{}", itemId);

        // Get the current user
        User currentUser = userService.getAuthenticatedUser();
        InventoryItem unapplyItem = inventoryService.getInventoryByOwnerIdAndItemId(currentUser.getUserId(),
                currentUser.getAppliedBadge().getId());

        if (unapplyItem.getItem().getId().equals(itemId)) {
            currentUser.setAppliedBadge(null);
            userService.saveUser(currentUser);
        }

        return "redirect:/inventory";
    }


    @PostMapping("/inventory/sell/item/{itemId}")
    public String sellItem(@PathVariable Long itemId) {
        logger.info("POST /inventory/sell/item/{}", itemId);
        User currentUser = userService.getAuthenticatedUser();

        // Get inventory
        List<InventoryItem> inventory = inventoryService.getUserInventory(currentUser);

        // Find item in inventory
        Optional<InventoryItem> matchingItemInInventory = inventory.stream()
                .filter(item -> item.getItem().getId().equals(itemId))
                .findFirst();

        if (matchingItemInInventory.isPresent()) {
            // Get the item
            Item item = matchingItemInInventory.get().getItem();

            // Calculate the resale price
            Integer resalePrice = (int) (item.getPrice() * 0.9); // 90% of the original price

            // Update user's balance
            currentUser.setBloomBalance(currentUser.getBloomBalance() + resalePrice);

            // Remove the item from the inventory
            inventoryService.removeInventoryItem(matchingItemInInventory.get());

            // Save the updated user
            userService.saveUser(currentUser);

            // Log success
            logger.info("User {} sold item {}", currentUser.getFirstName(), itemId);
        } else {
            // Item is not found
            logger.error("Item with ID {} not found", itemId);
        }

        return "redirect:/inventory";
    }


}
