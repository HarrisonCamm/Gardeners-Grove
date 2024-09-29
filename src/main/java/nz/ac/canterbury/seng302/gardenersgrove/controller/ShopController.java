package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    Logger logger = LoggerFactory.getLogger(ShopController.class);
    private final ShopService shopService;
    private final ItemService itemService;
    private final UserService userService;
    private final TransactionService transactionService;


    @Autowired
    public ShopController(ShopService shopService, ItemService itemService, UserService userService, TransactionService transactionService) {
        this.shopService = shopService;
        this.itemService = itemService;
        this.userService = userService;
        this.transactionService = transactionService;
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
        logger.info("GET /shop");
        RedirectService.addEndpoint("/shop");
        addShopItems(model);

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
        logger.info("POST /shop");
        RedirectService.addEndpoint("/shop");
        // Get the current user
        User currentUser = userService.getAuthenticatedUser();
        User gardenGroveUser = userService.getUserByEmail("gardenersgrove@email.com");
        addShopItems(model);

        // Validate the item exists
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            model.addAttribute("purchaseMessage", "Item not found.");
            return "redirect:/shop?error=Item not found";
        }

        // Attempt to purchase the item
        boolean isPurchased =  shopService.purchaseItem(currentUser, shopService.getShop(), item);


        // Redirect based on the purchase result
        if (isPurchased) {
            // Add a new transaction for the purchase
            transactionService.addTransaction(item.getPrice(),
                    "Purchased '" + item.getName() + "' item from the Shop",
                    "Shop Purchase",
                    gardenGroveUser.getUserId(),
                    currentUser.getUserId());
            currentUser = userService.getAuthenticatedUser();
            model.addAttribute("bloomBalance", currentUser.getBloomBalance());
            model.addAttribute("purchaseSuccessful", "Purchase successful");
            return "shopTemplate";
        } else {
            model.addAttribute("purchaseNotSuccessful", "Insufficient Bloom balance");
            return "shopTemplate";
        }
    }

    public void addShopItems(Model model) {
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
    }
}
