package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.service.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This is a basic spring boot controller, note the @link{Controller} annotation which defines this.
 * This controller defines endpoints as functions with specific HTTP mappings
 */
@Controller
public class HomeController {
    Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Redirects GET default url '/' to '/home'
     * @return redirect to /home
     */
    @GetMapping("/")
    public String home() {
        logger.info("GET /");
        return "redirect:./home";
    }

    /**
     * Gets the thymeleaf page representing the /demo page (a basic welcome screen with some links)
     * @param name url query parameter of user's name
     * @param model (map-like) representation of data to be used in thymeleaf display
     * @return thymeleaf demoTemplate
     */
    @GetMapping("/home")
    public String getTemplate(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        logger.info("GET /home");

        RedirectService.addEndpoint("/home");

        model.addAttribute("name", name);
        return "homeTemplate";
    }

}
