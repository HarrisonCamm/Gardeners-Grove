package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.BadgeItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.ImageItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.ItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ShopService;
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

    @Autowired
    public ShopController(ShopService shopService, ItemService itemService, UserService userService) {
        this.shopService = shopService;
        this.itemService = itemService;
        this.userService = userService;
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
        String purchaseResult = itemService.purchaseItem(itemId, currentUser.getUserId());

        // Redirect based on the purchase result
        if (purchaseResult.equals("Purchase successful")) {
            return "redirect:/shop?success=true";
        } else {
            return "redirect:/shop?error=Insufficient Bloom balance";
        }
    }
}
