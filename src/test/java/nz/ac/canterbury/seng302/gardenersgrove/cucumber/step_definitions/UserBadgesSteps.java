package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.ItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

import static org.junit.jupiter.api.Assertions.*;
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
    private AuthenticationManager authenticationManager;

    private MockMvc mockMvc;
    private MvcResult mvcResult;
    private ResultActions resultActions;
    private BadgeItem badgeItem;
    private User friend;

    @Before
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @And("I have items in my inventory")
    public void i_have_items_in_my_inventory() throws Exception {
        // Get the user Entity for Sarah
        User currentUser = userService.getAuthenticatedUser();
        List<Item> badgeItems = itemService.getBadgesByOwner(currentUser.getUserId());

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
    @When("I click on the Use button on that badge item")
    public void i_click_on_the_button_on_that_badge_item() throws Exception {

        badgeItem = (BadgeItem) itemService.getItemByName("Tim Tam");

        mockMvc.perform(post("/inventory/badge/use/{badgeId}", badgeItem.getId()))
                .andExpect(status().is3xxRedirection()) // Redirect to inventory to maintain URL
                .andReturn();
    }

    @Then("the badge is shown next to my name")
    public void the_badge_is_shown_next_to_my_name() throws Exception {
        BadgeItem userBadge = userService.getAuthenticatedUser().getAppliedBadge();
        BadgeItem badge = (BadgeItem) itemService.getItemByName("Tim Tam");
        assertEquals(userBadge.getName(), badge.getName());

        // In the view, the badge is retrieved when the page is loaded
        MvcResult result = mockMvc.perform(get("/get-image")
                        .param("userBadge", "true")
                        .param("userID", userService.getAuthenticatedUser().getUserId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the image data
        byte[] responseImageData = result.getResponse().getContentAsByteArray();
        Assertions.assertArrayEquals(userBadge.getIcon().getData(), responseImageData);
    }

    @Given("I have a badge item applied to my name")
    public void i_have_a_badge_item_applied_to_my_name() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        i_click_on_the_button_on_that_badge_item();
    }

    @When("another user views my name")
    public void another_user_views_my_name() throws Exception {
        // Because our user is #1 on the leaderboard, we can just view the leaderboard (main page)
        mvcResult = mockMvc.perform(get("/main"))
                .andExpect(view().name("mainTemplate"))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Then("they see the badge displayed next to my name")
    public void they_see_the_badge_displayed_next_to_my_name() {

        List<User> users = (List<User>) mvcResult.getModelAndView().getModel().get("topUsers");
        User currentUser = userService.getAuthenticatedUser();
        assertEquals(currentUser, users.get(0));
        assertNotNull(users.get(0).getAppliedBadge());
    }

    @Given("I am on my profile page")
    public void i_am_on_my_profile_page() throws Exception {
        mockMvc.perform(get("/view-user-profile"))
                .andExpect(view().name("viewUserProfileTemplate"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @When("I view my name")
    public void i_view_my_name() throws Exception {
        // In the view, the badge is retrieved when the page is loaded
        mvcResult = mockMvc.perform(get("/get-image")
                        .param("userBadge", "true")
                        .param("userID", userService.getAuthenticatedUser().getUserId().toString()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Then("the badge I have applied are displayed next to my name")
    public void the_badges_i_have_applied_are_displayed_next_to_my_name() {
        byte[] responseImageData = mvcResult.getResponse().getContentAsByteArray();
        Assertions.assertArrayEquals(
                userService.getAuthenticatedUser().getAppliedBadge().getIcon().getData(),
                responseImageData
        );
    }

    @Given("I have a friend {string} and they have a badge item applied to their name")
    public void i_have_a_friend_and_they_have_a_badge_item_applied_to_their_name(String friendEmail) throws Exception {
        User currentUser = userService.getAuthenticatedUser();
        friend = userService.getUserByEmail(friendEmail);

        currentUser.addFriend(friend);
        friend.addFriend(currentUser);
        userService.updateUserFriends(currentUser);
        userService.updateUserFriends(friend);

        // Log out
        SecurityContextHolder.clearContext();

        // Log in as the friend
        UsernamePasswordAuthenticationToken friendToken = new UsernamePasswordAuthenticationToken(friend.getEmail(), "Password1!");
        var friendAuthentication = authenticationManager.authenticate(friendToken);
        SecurityContextHolder.getContext().setAuthentication(friendAuthentication);

        // Apply a badge to the friend
        i_click_on_the_button_on_that_badge_item();

        // Log out and log in as the original user
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(currentUser.getEmail(), "Password1!");
        var authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @When("I view their profile")
    public void i_view_their_profile() throws Exception {
        mvcResult = mockMvc.perform(get("/manage-friends"))
                .andExpect(view().name("manageFriendsTemplate"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Then("I see the badges displayed next to their name")
    public void i_see_the_badges_displayed_next_to_their_name() throws Exception {
        BadgeItem friendBadge = userService.getUserByEmail(friend.getEmail()).getAppliedBadge();

        // In the view, the badge is retrieved when the page is loaded
        MvcResult result = mockMvc.perform(get("/get-image")
                        .param("userBadge", "true")
                        .param("userID", friend.getUserId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the image data
        byte[] responseImageData = result.getResponse().getContentAsByteArray();
        Assertions.assertArrayEquals(friendBadge.getIcon().getData(), responseImageData);
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
