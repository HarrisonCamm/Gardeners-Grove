package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.persistence.Tuple;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.InventoryService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.*;
import java.util.stream.Collectors;

@Controller
public class InventoryController {
    Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private final ItemService itemService;
    private final UserService userService;
    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(ItemService itemService, UserService userService, InventoryService inventoryService) {
        this.itemService = itemService;
        this.userService = userService;
        this.inventoryService = inventoryService;
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
}
