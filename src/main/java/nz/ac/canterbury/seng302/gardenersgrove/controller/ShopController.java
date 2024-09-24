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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ShopController {
    private final ShopService shopService;
    private UserService userService;

    private ItemService itemService;

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

    @GetMapping("/add-item-to-user")
    public String addItemToUser(@RequestParam Long userId, @RequestParam Item item) {
        User user = userService.getUserByID(userId);


    }


    public String updateUserProfile(@RequestParam Long userId, @RequestParam String imageURL) {
        User user = userService.getUserByID(userId);

        user.setImageURL(imageURL);
        userService.saveUser(user);
        return "redirect:/inventory";
    }

}
