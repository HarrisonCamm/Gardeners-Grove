package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.BadgeItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.ImageItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.ItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ShopService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
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
    Logger logger = LoggerFactory.getLogger(ShopService.class);
    private final ShopService shopService;
    private UserService userService;

    private ItemService itemService;

    private RedirectService redirectService;

    @Autowired
    public ShopController(ShopService shopService, UserService userService, ItemService itemService) {
        this.shopService = shopService;
        this.userService = userService;
        this.itemService = itemService;
    }


    /**
     * Retrieves badge and image items for the shop
     * and adds them to the model
     * @param model
     * @return
     */
    @GetMapping("/shop")
    public String shopPage(Model model) {
        logger.info("GET /shop");
        RedirectService.addEndpoint("/shop");

        User user = userService.getAuthenticatedUser();  // Fetch user by ID and add it to the model
        model.addAttribute("user", user);

        Set<Item> availableItems = shopService.getItemsInShop();

        // Filter BadgeItem and ImageItem
        Set<Item> badgeItems = availableItems.stream()
                .filter(BadgeItem.class::isInstance)
                .collect(Collectors.toSet());

        Set<Item> imageItems = availableItems.stream()
                .filter(ImageItem.class::isInstance)
                .collect(Collectors.toSet());

        model.addAttribute("user", user);
        model.addAttribute("badgeItems", badgeItems);
        model.addAttribute("imageItems", imageItems);

        return "shopTemplate";  // Return the shop template
    }


    @PostMapping("/shop/buy")
    public String addItemToUser(@RequestParam Long userId, @RequestParam Long itemId) {
        User user = userService.getUserByID(userId);
        Item item = itemService.getItemById(itemId);
        shopService.purchaseItem(user, item);
        return "redirect:/shop";
    }


    public String updateUserProfile(@RequestParam Long userId, @RequestParam String imageURL) {
        User user = userService.getUserByID(userId);

        user.setImageURL(imageURL);
        userService.saveUser(user);
        return "redirect:/shop";
    }

}
