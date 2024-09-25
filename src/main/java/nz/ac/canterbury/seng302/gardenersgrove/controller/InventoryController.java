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

        // DUMMY DATA
        if (badgeItems.isEmpty()) {
            currentUser.addItem(itemService.getItemByName("Happy"));
            currentUser.addItem(itemService.getItemByName("Eggplant"));
            userService.addUser(currentUser);
        }
        if (imageItems.isEmpty()) {
            currentUser.addItem(itemService.getItemByName("Cat Fall"));
            currentUser.addItem(itemService.getItemByName("Cat Typing"));
            currentUser.addItem(itemService.getItemByName("Fabian Intensifies"));
            userService.addUser(currentUser);
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

        try {
            // Gets item, then casts to ImageItem
            ImageItem imageItem = (ImageItem) itemService.getItemById(itemId);

            // Get the image of imageItem
            Long itemImageId = imageItem.getImage().getId();

            // Get image from Image Table
            Optional<Image> image = imageService.findImage(itemImageId);

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
