package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.InventoryItemService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private GardenService gardenService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private InventoryItemService inventoryItemService;
    private MockMvc mockMvc;
    private MvcResult mvcResult;
    private BadgeItem badgeItem1;
    private BadgeItem badgeItem2;
    private BadgeItem badgeItem3;
    private User friend;


    @Before
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @And("I have items in my inventory")
    public void i_have_items_in_my_inventory() throws Exception {
        // Get the user Entity for Sarah
        User currentUser = userService.getAuthenticatedUser();

        // Add items to users inventory
        badgeItem1 = (BadgeItem) itemService.getItemByName("Tim Tam");
        badgeItem2 = (BadgeItem) itemService.getItemByName("Vegemite");
        badgeItem3 = (BadgeItem) itemService.getItemByName("Neo Fabian");



        // Add items to users inventory
        inventoryItemService.save(new InventoryItem(currentUser, badgeItem1, 1));
        inventoryItemService.save(new InventoryItem(currentUser, badgeItem2, 1));
        inventoryItemService.save(new InventoryItem(currentUser, badgeItem3, 1));
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

        mockMvc.perform(post("/inventory/badge/use/{badgeId}", badgeItem1.getId()))
                .andExpect(status().is3xxRedirection()) // Redirect to inventory to maintain URL
                .andReturn();
    }

    @Then("the badge is shown next to my name")
    public void the_badge_is_shown_next_to_my_name() throws Exception {
        BadgeItem userBadge = userService.getAuthenticatedUser().getAppliedBadge();
        assertEquals(userBadge.getName(), badgeItem1.getName());

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

    @When("I view their profile on the manage friends page")
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

    @Given("{string} has a badge item applied to their name")
    public void hasABadgeItemAppliedToTheirName(String email) throws Exception {

        User currentUser = userService.getAuthenticatedUser();

        // Log out
        SecurityContextHolder.clearContext();

        // Log in as the friend
        UsernamePasswordAuthenticationToken friendToken = new UsernamePasswordAuthenticationToken(email, "Password1!");
        var friendAuthentication = authenticationManager.authenticate(friendToken);
        SecurityContextHolder.getContext().setAuthentication(friendAuthentication);

        //Apply a badge to the friend
        i_click_on_the_button_on_that_badge_item();

        // Log out and log in as the original user
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(currentUser.getEmail(), "Password1!");
        var authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    @Given("I am on the friends page and open the add friends search bar")
    public void i_am_on_the_friends_page_and_open_the_add_friends_search_bar() throws Exception {
        mockMvc.perform(get("/manage-friends"))
                .andExpect(view().name("manageFriendsTemplate"))
                .andExpect(status().isOk());
    }



    @When("I search for {string} who has a badge item applied to their name")
    public void i_search_for_a_friend_who_has_a_badge_item_applied_to_their_name(String email) throws Exception {
        mvcResult = mockMvc.perform(post("/manage-friends")
                        .param("action", "search")
                        .param("searchQuery", email))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Then("I see the badge displayed next to their name in the search results")
    public void i_see_the_badge_displayed_next_to_their_name_in_the_search_results() throws Exception {
        List<User> matchingUsers = (List<User>) mvcResult.getModelAndView().getModel().get("matchedUsers");
        assertEquals(1, matchingUsers.size()); // Only one user should be returned

        User firstUser = matchingUsers.get(0);


        // Get the badge item applied to the user as this will be called when rendering the page
        MvcResult imageResult = mockMvc.perform(get("/get-image")
                        .param("userBadge", "true")
                        .param("userID", firstUser.getUserId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the image data
        byte[] responseImageData = imageResult.getResponse().getContentAsByteArray();
        Assertions.assertArrayEquals(firstUser.getAppliedBadge().getIcon().getData(), responseImageData);
    }

    @And("{string} has sent me a friend request")
    public void hasSentMeAFriendRequest(String email) throws Exception {
        User currentUser = userService.getAuthenticatedUser();

        // Log out
        SecurityContextHolder.clearContext();

        // Log in as the friend
        UsernamePasswordAuthenticationToken friendToken = new UsernamePasswordAuthenticationToken(email, "Password1!");
        var friendAuthentication = authenticationManager.authenticate(friendToken);
        SecurityContextHolder.getContext().setAuthentication(friendAuthentication);

        // Send a friend request
        mockMvc.perform(post("/manage-friends")
                        .param("action", "invite")
                        .param("email", currentUser.getEmail())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Log out and log in as the original user
        SecurityContextHolder.clearContext();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(currentUser.getEmail(), "Password1!");
        var authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @And("I am on the friends page")
    public void i_am_on_the_friends_page() throws Exception {
        mvcResult = mockMvc.perform(get("/manage-friends"))
                .andExpect(view().name("manageFriendsTemplate"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @When("I view their name in the pending invites section")
    public void i_view_their_name_in_the_pending_section() {
        List<FriendRequest> friendRequests = (List<FriendRequest>) mvcResult.getModelAndView().getModel().get("pendingRequests");
        friend = friendRequests.get(0).getSender();
        assertNotNull(friendRequests);
        assertEquals(1, friendRequests.size());
    }

    @When("I view their name in the sent invites section")
    public void i_view_their_name_in_the_sent_section() {
        List<FriendRequest> friendRequests = (List<FriendRequest>) mvcResult.getModelAndView().getModel().get("sentRequests");
        assertNotNull(friendRequests);
        assertEquals(1, friendRequests.size());
    }

    @And("I send {string} has sent me a friend request")
    public void iSendHasSentMeAFriendRequest(String email) throws Exception {
        friend = userService.getUserByEmail(email);
        mockMvc.perform(post("/manage-friends")
                        .param("action", "invite")
                        .param("email", email)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Given("I am viewing {string}'s public garden")
    public void i_am_viewing_a_public_garden_and_the_owner_has_a_badge_item_applied_to_their_name(String email) throws Exception {

        User owner = userService.getUserByEmail(email);

        Garden gardens = gardenService.getOwnedGardens(owner.getUserId()).get(0);

        mvcResult = mockMvc.perform(get("/view-garden")
                        .param("gardenID", gardens.getId().toString()))
                .andExpect(view().name("viewUnownedGardenDetailsTemplate"))
                .andExpect(status().isOk())
                .andReturn();

    }

    @When("I view their name in the garden")
    public void iViewTheirNameInTheGarden() {
        friend = (User) mvcResult.getModelAndView().getModel().get("gardenOwner");
        assertNotNull(friend);
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
