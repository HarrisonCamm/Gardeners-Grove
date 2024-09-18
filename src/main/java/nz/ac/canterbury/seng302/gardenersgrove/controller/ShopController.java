package nz.ac.canterbury.seng302.gardenersgrove.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShopController {

    @GetMapping("/shop")
    public String shopPage() {
        return "shopTemplate";  // This will return the shopTemplate.html from the templates folder
    }
}
