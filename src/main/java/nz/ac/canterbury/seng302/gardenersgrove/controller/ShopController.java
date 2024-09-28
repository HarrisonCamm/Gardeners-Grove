package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.ItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ShopService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TransactionService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ShopController {
    private final ShopService shopService;
    private final ItemService itemService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final User gardenGroveUser;

    // Injecting EntityManager
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ShopController(ShopService shopService, ItemService itemService, UserService userService, TransactionService transactionService) {
        this.shopService = shopService;
        this.itemService = itemService;
        this.userService = userService;
        this.transactionService = transactionService;
        this.gardenGroveUser = userService.getUserByEmail("gardenersgrove@email.com");
    }

    /**
     * Retrieves badge and image items for the shop
     * and adds them to the model.
     *
     * @param model the model
     * @return the shop page template
     */
    @GetMapping("/shop")
    public String shopPage(Model model) {
        Set<Item> availableItems = shopService.getItemsInShop();

        // Filter BadgeItem and ImageItem
        Set<Item> badgeItems = availableItems.stream()
                .filter(BadgeItem.class::isInstance)
                .collect(Collectors.toSet());

        Set<Item> imageItems = availableItems.stream()
                .filter(ImageItem.class::isInstance)
                .collect(Collectors.toSet());

        model.addAttribute("badgeItems", badgeItems);
        model.addAttribute("imageItems", imageItems);

        return "shopTemplate";  // This will return the shopTemplate.html from the templates folder
    }

    /**
     * Handles purchasing an item from the shop.
     *
     * @param itemId the ID of the item to purchase
     * @param model  the model
     * @return the shop page or redirects based on the purchase outcome
     */
    @PostMapping("/shop")
    public String purchaseItem(@RequestParam("itemId") Long itemId, Model model) {
        // Get the current user
        User currentUser = userService.getAuthenticatedUser();

        // Validate the item exists
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            model.addAttribute("purchaseMessage", "Item not found.");
            return "redirect:/shop?error=Item not found";
        }

        // Attempt to purchase the item
        boolean isPurchased =  shopService.purchaseItem(currentUser, ShopService.getShopInstance(entityManager), item);


        // Redirect based on the purchase result
        if (isPurchased) {
            // Add a new transaction for the purchase
            Transaction transaction = transactionService.addTransaction(item.getPrice(),
                    "Purchased " + item.getName() + " from the Shop",
                    "Shop Purchase",
                    gardenGroveUser.getUserId(),
                    currentUser.getUserId());

            return "redirect:/shop?success=true";
        } else {
            return "redirect:/shop?error=Insufficient Bloom balance";
        }
    }
}
