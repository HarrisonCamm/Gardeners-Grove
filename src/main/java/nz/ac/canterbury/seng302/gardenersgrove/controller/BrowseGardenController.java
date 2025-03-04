package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.exceptions.PageNumberTooHighException;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Controller
public class BrowseGardenController {
    Logger logger = LoggerFactory.getLogger(BrowseGardenController.class);
    private final GardenService gardenService;
    private final UserService userService;
    private final TagService tagService;
    List<Long> tagIds = new ArrayList<>();
    List<Tag> displayedSearchTags = new ArrayList<>();

    @Autowired
    public BrowseGardenController(GardenService gardenService, UserService userService, TagService tagService) {
        this.gardenService = gardenService;
        this.userService = userService;
        this.tagService = tagService;
    }

    /**
     * Gets the gardens to be displayed
     *
     * @param page page number
     * @param query the typed search string of a garden by a user
     * @param tagName the tag selected or typed by the user to search for
     * @return thymeleaf browseGardensTemplate
     */
    @GetMapping("/browse-gardens")
    public String browseGardens(HttpSession session,
                         @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                         @RequestParam(value = "q", required = false, defaultValue = "") String query,
                         @RequestParam(value = "tagsInput", required = false, defaultValue = "") String tagName,
                         Model model) {

        logger.info("GET /browse-gardens");
        RedirectService.addEndpoint("/browse-gardens");

        // Fetch and add allTags to the model for autocomplete
        List<Tag> allTags = tagService.getTagsByEvaluated(true);
        model.addAttribute("allTags", allTags);

        if (page < 1) {
            return "redirect:/browse-gardens";
        }


        if (!tagName.isEmpty() ) {
            Tag tag = tagService.getTagByName(tagName);
            if(tag != null) {
                if (displayedSearchTags.stream().noneMatch(existingTag -> existingTag.getId().equals(tag.getId()))) {
                    displayedSearchTags.add(tag);
                    tagIds.add(tag.getId());
                }
            }else {
                model.addAttribute("tagNotFoundError", "No tag matching " + tagName);
            }
        }

        displayTags(query, page, model, tagName);


        return "browseGardensTemplate";
    }

    /**
     * To process a tag that is to be removed from the selected search tags
     *
     * @param tagNameToRemove the name of the tag to be removed
     * @param page page number
     * @param query the typed search string of a garden by a user
     * @param tagName the tag selected or typed by the user to search for
     * @return thymeleaf browseGardensTemplate
     */
    @PostMapping("/browse-gardens")
    public String browseGardens(@RequestParam(value="tagToRemove") String tagNameToRemove,
                                @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                @RequestParam(value = "q", required = false, defaultValue = "") String query,
                                @RequestParam(value = "tagsInput", required = false, defaultValue = "") String tagName,
                                Model model) {
        logger.info("POST /browse-gardens");
        RedirectService.addEndpoint("/browse-gardens");

        // Fetch and add allTags to the model for autocomplete
        List<Tag> allTags = tagService.getTagsByEvaluated(true);
        model.addAttribute("allTags", allTags);

        int tagIndex = IntStream.range(0, displayedSearchTags.size())
                .filter(i -> displayedSearchTags.get(i).getName().equals(tagNameToRemove))
                .findFirst()
                .orElse(-1);

        // Prevent -1 index out of range error
        if (!displayedSearchTags.isEmpty() &&
                displayedSearchTags.stream().anyMatch(eachTag -> eachTag.getName().equals(tagNameToRemove))) {
            Tag tagToRemove = displayedSearchTags.get(tagIndex);
            displayedSearchTags.remove(tagToRemove);
            tagIds.remove(tagToRemove.getId());
        }

        displayTags(query, page, model, tagName);
        return "browseGardensTemplate";
    }

    /**
     * Handles adding the relevant attributes to the model to display
     * @param page page number
     * @param query the typed search string of a garden by a user
     * @param tagName the tag selected or typed by the user to search for
     * @param model the model to be passed to the browse gardens view
     */
    public String displayTags(String query, int page, Model model, String tagName) {
        Page<Garden> gardenPage;
        if (!query.isEmpty() && !displayedSearchTags.isEmpty()) { //query and tag entered
            gardenPage = gardenService.searchPublicGardensBySearchAndTags(query, page-1, tagIds);
        } else if (!displayedSearchTags.isEmpty()) { //tags entered but no query
            gardenPage = gardenService.searchPublicGardensByTags(tagIds, page - 1);
        } else {
            gardenPage = gardenService.searchPublicGardens(query, page - 1);
        }

        if (gardenPage.getTotalElements() == 0) {
            model.addAttribute("noResults", "No gardens match your search");
        } else if (gardenPage.getNumber() >= gardenPage.getTotalPages()) {
            throw new PageNumberTooHighException("Page number too high");
        }
        model.addAttribute("gardenPage", gardenPage);
        model.addAttribute("q", query);

        if (model.containsAttribute("tagNotFoundError")) {
            model.addAttribute("tagsInput", tagName); //text field not cleared if tag doesn't exist
        } else {
            model.addAttribute("tagsInput", "");
        }
        model.addAttribute("searchTags", displayedSearchTags);

        return query;
    }

    @ExceptionHandler(PageNumberTooHighException.class)
    public String handlePageNumberTooHighException() {
        return "redirect:/browse-gardens";
    }

}

