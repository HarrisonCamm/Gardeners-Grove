package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class InventoryController {
    Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @GetMapping("/inventory")
    public String getTemplate(Model model) {
        logger.info("GET /inventory");
        RedirectService.addEndpoint("/inventory");

        //Create and populate a list of items for the view to render
        List<String[]> badgeItems = new ArrayList<>();

        // TODO - Simulating the adding of items, this will be done using service and repo layers in another task
        // uncomment these to view how it would look, commented out for tests to work
        badgeItems.add(new String[]{"1x", "vegemite.png", "Vegemite"});
        badgeItems.add(new String[]{"1x", "timtam.png", "Tim Tam"});
        badgeItems.add(new String[]{"1x", "neo_fabian.png", "Neo Fabian"});

        //Create and populate a list of items for the view to render
        List<String[]> gifItems = new ArrayList<>();

        // TODO - Simulating the adding of items, this will be done using service and repo layers in another task
        gifItems.add(new String[]{"1x", "fabian.gif", "Fabian Intensifies"});
        gifItems.add(new String[]{"1x", "scrum_master_harrison.gif", "Scrum Master Harrison"});
        gifItems.add(new String[]{"1x", "stick_man.gif", "Stick Man"});


        model.addAttribute("badgeItems", badgeItems);
        model.addAttribute("gifItems", gifItems);


        return "inventoryTemplate";
    }
}
