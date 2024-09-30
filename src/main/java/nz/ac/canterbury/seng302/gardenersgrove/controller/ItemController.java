package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.BadgeItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ItemController {
    Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/item")
    public String getTemplate(@RequestParam("itemID") Long itemID,
            Model model) {
        logger.info("GET /item");
        RedirectService.addEndpoint("/item");

        Item item = itemService.getItemById(itemID);

        if (item == null) {
            return "redirect:/inventory";
        }

        boolean isBadge = item instanceof BadgeItem;
        Integer resalePrice = (int) (item.getPrice() * 0.9); //todo integrate with AC9 work

        model.addAttribute("item", item);
        model.addAttribute("isBadge", isBadge); //if not a badge, it is an ImageItem
        model.addAttribute("originalPriceText", item.getPrice() + " ฿");
        model.addAttribute("resalePriceText", resalePrice + " ฿");

        return "itemDetailsTemplate";
    }

}
