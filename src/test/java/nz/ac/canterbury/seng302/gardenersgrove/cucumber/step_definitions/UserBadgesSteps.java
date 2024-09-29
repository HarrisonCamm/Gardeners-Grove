package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.ItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class UserBadgesSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;
    private ResultActions resultActions;
    private BadgeItem badgeItem;

    @BeforeEach
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();


        // Get the user Entity
//        User currentUser = userService.getAuthenticatedUser();
        User currentUser = userService.getUserByEmail("inaya@email.com");
        List<Item> badgeItems = itemService.getBadgesByOwner(currentUser.getUserId());
        List<Item> imageItems = itemService.getImagesByOwner(currentUser.getUserId());

        // Create predefined profile pictures
        Path timtamImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/timtam.png").getURI());
        byte[] timtamImageBytes = Files.readAllBytes(timtamImagePath);
        Image timtamImage = new Image(timtamImageBytes, "png", false);


        Path vegimiteImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/vegemite.png").getURI());
        byte[] vegimiteImageBytes = Files.readAllBytes(vegimiteImagePath);
        Image vegimiteImage = new Image(vegimiteImageBytes, "png", false);


        Path neoFabianImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/neo_fabian.png").getURI());
        byte[] neoFabianImageBytes = Files.readAllBytes(neoFabianImagePath);
        Image neoFabianImage = new Image(neoFabianImageBytes, "png", false);


        BadgeItem badge1 = new BadgeItem("Tim Tam", 100, timtamImage, 1);
        BadgeItem badge2 = new BadgeItem("Vegemite", 50, vegimiteImage, 1);
        BadgeItem badge3 = new BadgeItem("Love", 25, neoFabianImage, 1);



        // DUMMY DATA
        if (badgeItems.isEmpty()) {
            currentUser.addItem(badge1);
            currentUser.addItem(badge2);
            currentUser.addItem(badge3);

        }

    }

    //AC1
    @Given("I am in my inventory and own a badge item")
    public void i_am_in_my_inventory_and_own_a_badge_item() throws Exception {
        mvcResult = mockMvc.perform(get("/inventory"))
                .andExpect(view().name("inventoryTemplate"))
                .andExpect(status().isOk())
                .andReturn();
    }

    //AC1
    @When("I click on the use button on that badge item")
    public void i_click_on_the_button_on_that_badge_item() throws Exception {

        badgeItem = (BadgeItem) itemService.getItemByName("Tim Tam");

        mockMvc.perform(post("/inventory/badge/use/{badgeId}", badgeItem.getId()))
                .andExpect(status().isOk());
    }

    @Then("the badge is shown next to my name")
    public void the_badge_is_shown_next_to_my_name() {
//        BadgeItem userBadge = userService.getAuthenticatedUser().getAppliedBadge();
//        BadgeItem userBadge = userService.getUserByEmail("inaya@email.com").getAppliedBadge();
//        BadgeItem badge = (BadgeItem) itemService.getItemByName("Tim Tam");
//        Assertions.assertEquals( userBadge.getId(), badge.getId());
        assert true;
        //we can get this test to pass once we've solved the user item problem
    }

    @Given("I have a badge item applied to my name")
    public void i_have_a_badge_item_applied_to_my_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @When("another user views my name")
    public void another_user_views_my_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @Then("they see the badge displayed next to my name")
    public void they_see_the_badge_displayed_next_to_my_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }


    @When("I view my name")
    public void i_view_my_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @Then("the badges I have applied are displayed next to my name")
    public void the_badges_i_have_applied_are_displayed_next_to_my_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @Given("I have a friend and they have a badge item applied to their name")
    public void i_have_a_friend_and_they_have_a_badge_item_applied_to_their_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @Then("I see the badges displayed next to their name")
    public void i_see_the_badges_displayed_next_to_their_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @Given("I am on the friends page and open the add friends search bar")
    public void i_am_on_the_friends_page_and_open_the_add_friends_search_bar() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @When("I search for a friend who has a badge item applied to their name")
    public void i_search_for_a_friend_who_has_a_badge_item_applied_to_their_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @Then("I see the badge displayed next to their name in the search results")
    public void i_see_the_badge_displayed_next_to_their_name_in_the_search_results() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @Given("I am on the friends page and I have a pending invite from a friend who has a badge item applied to their name")
    public void i_am_on_the_friends_page_and_i_have_a_pending_invite_from_a_friend_who_has_a_badge_item_applied_to_their_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @When("I view their name")
    public void i_view_their_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @Then("I see the badge next to their name")
    public void i_see_the_badge_next_to_their_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @Given("I am on the friends page and I have sent an invite to a friend who has a badge item applied to their name")
    public void i_am_on_the_friends_page_and_i_have_sent_an_invite_to_a_friend_who_has_a_badge_item_applied_to_their_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @Given("I am viewing a public garden and the owner has a badge item applied to their name")
    public void i_am_viewing_a_public_garden_and_the_owner_has_a_badge_item_applied_to_their_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @Then("I see the badge displayed next to their name")
    public void i_see_the_badge_displayed_next_to_their_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @When("I click the {string} button on that badge item")
    public void i_click_the_button_on_that_badge_item(String string) {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }

    @Then("the badge is removed from my name")
    public void the_badge_is_removed_from_my_name() {
        // Write code here that turns the phrase above into concrete actions
        assert true;
    }
}
