package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.BadgeItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.ImageItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.ItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"unchecked", "SpringJavaInjectionPointsAutowiringInspection"})
public class UnlockableContentSteps {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    private ResultActions resultActions;
    private MvcResult mvcResult;
    private User currentUser;

    @Before
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        User currentUser = userService.getAuthenticatedUser();
    }

    @When("I click Inventory")
    public void i_click_inventory() throws Exception {
        resultActions = mockMvc.perform(get("/inventory"));
        mvcResult = resultActions.andExpect(status().isOk())
                .andReturn();
    }

    // AC1
    @Then("I am shown my inventory of items")
    public void i_am_shown_my_inventory_of_items() {
        // Retrieve items from the model
        List<Item> badgeItems = (List<Item>) mvcResult.getModelAndView().getModel().get("badgeItems");
        List<Item> imageItems = (List<Item>) mvcResult.getModelAndView().getModel().get("imageItems");

        // Retrieve the current user
        User currentUser = userService.getAuthenticatedUser();

        // Fetch expected owned items from the services
        List<Item> expectedOwnedBadgeItems = itemService.getBadgesByOwner(currentUser.getUserId());
        List<Item> expectedOwnedImageItems = itemService.getImagesByOwner(currentUser.getUserId());

        // Assertions to verify the inventory
        Assertions.assertEquals(expectedOwnedBadgeItems.size(), badgeItems.size(), "Badge items count does not match.");
        Assertions.assertTrue(badgeItems.containsAll(expectedOwnedBadgeItems), "Badge items do not match the expected owned items.");

        Assertions.assertEquals(expectedOwnedImageItems.size(), imageItems.size(), "Image items count does not match.");
        Assertions.assertTrue(imageItems.containsAll(expectedOwnedImageItems), "Image items do not match the expected owned items.");
    }

    // AC2
    @When("I click Shop")
    public void i_click_shop() throws Exception {
        resultActions = mockMvc.perform(get("/shop"));
        mvcResult = resultActions.andExpect(status().isOk())
                .andReturn();
    }

    // AC2
    @Then("I am shown the shop")
    public void i_am_shown_the_shop() {
        Set<Item> badgeItems = (Set<Item>) mvcResult.getModelAndView().getModel().get("badgeItems");
        Set<Item> imageItems = (Set<Item>) mvcResult.getModelAndView().getModel().get("imageItems");
        assertNotNull(badgeItems);
        assertNotNull(imageItems);
    }

    // AC3
    @Given("I am in the shop")
    public void i_am_in_the_shop() throws Exception {
        i_click_shop();
    }

    // AC3
    @Then("I can see a list of items for sale with a picture, name and price in Blooms")
    public void i_can_see_a_list_of_items_for_sale_with_a_picture_name_and_price_in_blooms() {
        Set<BadgeItem> badgeItemSet = (Set<BadgeItem>) mvcResult.getModelAndView().getModel().get("badgeItems");
        List<BadgeItem> badgeItems = new ArrayList<>(badgeItemSet);

        // AI Assisted Converted Stream for Assertions.assertAll functionality
        List<Executable> badgeItemAssertions = badgeItems.stream()
                .flatMap(badgeItem -> Stream.of(
                        (Executable) () -> Assertions.assertNotNull(badgeItem.getName(), "BadgeItem name should not be null"),
                        (Executable) () -> Assertions.assertNotNull(badgeItem.getPrice(), "BadgeItem price should not be null"),
                        (Executable) () -> Assertions.assertNotNull(badgeItem.getEmoji(), "BadgeItem emoji should not be null")
                ))
                .collect(Collectors.toList());

        Assertions.assertAll("BadgeItem assertions", badgeItemAssertions);

        Set<ImageItem> imageItemSet = (Set<ImageItem>) mvcResult.getModelAndView().getModel().get("imageItems");
        List<ImageItem> imageItems = new ArrayList<>(imageItemSet);

        // AI Assisted Converted Stream for Assertions.assertAll functionality
        List<Executable> imageItemAssertions = imageItems.stream()
                .flatMap(imageItem -> Stream.of(
                        (Executable) () -> Assertions.assertNotNull(imageItem.getName(), "ImageItem name should not be null"),
                        (Executable) () -> Assertions.assertNotNull(imageItem.getPrice(), "ImageItem price should not be null"),
                        (Executable) () -> Assertions.assertNotNull(imageItem.getImage(), "ImageItem image should not be null")
                ))
                .collect(Collectors.toList());

        Assertions.assertAll("ImageItem assertions", imageItemAssertions);
    }

    @Given("I am in my inventory")
    public void i_am_in_my_inventory() {
        // TODO: Setup initial context for being in inventory
    }

    @Then("I can see a list of my items I have purchased that have a picture, name and quantity")
    public void i_can_see_a_list_of_my_items_i_have_purchased_that_have_a_picture_name_and_quantity() {
        // TODO: Implement logic for displaying purchased items
    }

    @When("I attempt to buy an item costing more than my current Blooms balance")
    public void i_attempt_to_buy_an_item_costing_more_than_my_current_blooms_balance() {
        User currentUser = userService.getAuthenticatedUser();

        Item item = itemService.getAllItems().iterator().next();
        currentUser.setBloomBalance(100);
        item.setPrice(200);
        itemService.purchaseItem(item.getId(), currentUser.getUserId());
    }

    @Then("I am shown the error message {string}")
    public void i_am_shown_the_error_message(String expectedMessage) {

        // Retrieve the current user and item
        User currentUser = userService.getAuthenticatedUser();
        Item item = itemService.getAllItems().iterator().next();

        // Broke user
        currentUser.setBloomBalance(0);
        userService.saveUser(currentUser);

        item.setPrice(10000); // Set item price higher than user's balance
        itemService.saveItem(item);

        // Attempt to purchase the item
        String purchaseResult = itemService.purchaseItem(item.getId(), currentUser.getUserId());

        // Assert the expected result
        assertEquals(expectedMessage, purchaseResult);
    }

    @And("the item is not added to my items")
    public void the_item_is_not_added_to_my_items() {

        User currentUser = userService.getAuthenticatedUser();
        Item item = itemService.getAllItems().iterator().next();

        //old item list
        List<Item> ownedItems = itemService.getItemsByOwner(currentUser.getUserId());

        //try purchase item
        itemService.purchaseItem(item.getId(), currentUser.getUserId());

        //new item list
        List<Item> newOwnedItems = itemService.getItemsByOwner(currentUser.getUserId());

        //assert that the item is not added to the user's items
        assertEquals(ownedItems.size(), newOwnedItems.size());
    }

    @When("I buy an item costing less than or equal to my current Blooms balance")
    public void i_buy_an_item_costing_less_than_or_equal_to_my_current_blooms_balance() {

        User currentUser = userService.getAuthenticatedUser();

        Item item = itemService.getAllItems().iterator().next();
        currentUser.setBloomBalance(1000);
        item.setPrice(100);
        itemService.purchaseItem(item.getId(), currentUser.getUserId());
    }

    @Then("that item is added to my inventory")
    public void that_item_is_added_to_my_inventory() {

        User currentUser = userService.getAuthenticatedUser();
        Item item = itemService.getAllItems().iterator().next();

        //try purchase item
        itemService.purchaseItem(item.getId(), currentUser.getUserId());
        itemService.saveItem(item);

        //owned item list
        List<Item> ownedItems = itemService.getItemsByOwner(currentUser.getUserId());

        //assert that the item is not added to the user's items
        assertTrue(ownedItems.contains(item));
    }

    @And("the items cost in Blooms is deducted from my account")
    public void the_items_cost_in_blooms_is_deducted_from_my_account() {

        // Retrieve the current user
        User currentUser = userService.getAuthenticatedUser();

        // Retrieve the item to be purchased
        Item item = itemService.getAllItems().iterator().next();

        // Store the old Bloom balance and item price
        int oldBloomBalance = 10000;
        int itemPrice = item.getPrice();

        // Perform the purchase (which should update the user's balance)
        itemService.purchaseItem(item.getId(), currentUser.getUserId());

        // Re-fetch the user to ensure the balance is updated
        currentUser = userService.getAuthenticatedUser();

        // Get the updated Bloom balance
        int newBloomBalance = currentUser.getBloomBalance();

        // Assert that the new balance is correct
        assertEquals(oldBloomBalance - itemPrice, newBloomBalance);

    }

    @And("I am shown a confirmation message {string}")
    public void i_am_shown_a_confirmation_message(String expectedMessage) {

        // Retrieve the current user and item
        User currentUser = userService.getAuthenticatedUser();
        Item item = itemService.getAllItems().iterator().next();

        // Give user hella bank
        currentUser.setBloomBalance(1000);
        userService.saveUser(currentUser);

        item.setPrice(100);
        itemService.saveItem(item);

        // Attempt to purchase the item
        String purchaseResult = itemService.purchaseItem(item.getId(), currentUser.getUserId());

        // Assert the expected result
        assertEquals(expectedMessage, purchaseResult);

    }

    @Given("I have more than one of the same item")
    public void i_have_more_than_one_of_the_same_item() {
        // TODO: Setup context for having multiple of the same item
    }

    @When("I view my inventory")
    public void i_view_my_inventory() {
        // TODO: Implement logic for viewing inventory
    }

    @Then("the quantity is displayed alongside the item rather than displaying multiple instances of the item")
    public void the_quantity_is_displayed_alongside_the_item_rather_than_displaying_multiple_instances_of_the_item() {
        // TODO: Ensure the quantity is displayed properly
    }

    @When("I click on an item")
    public void i_click_on_an_item() {
        // TODO: Implement logic for clicking on an item
    }

    @Then("I am taken to a page for that item which displays more information on the item including picture, name, description, original price, and resale price")
    public void i_am_taken_to_a_page_for_that_item_which_displays_more_information_on_the_item_including_picture_name_description_original_price_and_resale_price() {
        // TODO: Implement logic for displaying detailed item page
    }

    @Given("I am viewing an item in my inventory")
    public void i_am_viewing_an_item_in_my_inventory() {
        // TODO: Setup context for viewing an item in inventory
    }

    @When("I click the Sell button for that item")
    public void i_click_the_sell_button_for_that_item() {
        // TODO: Implement logic for selling an item
    }

    @Then("a confirmation popup with a cancel button and confirm button is shown with the message {string}")
    public void a_confirmation_popup_with_a_cancel_button_and_confirm_button_is_shown_with_the_message(String arg0) {
        // TODO: Display confirmation popup for selling an item
    }

    @Given("I purchase an item")
    public void i_purchase_an_item() {
        // TODO: Setup context for purchasing an item
    }

    @When("I check my Bloom transaction history")
    public void i_check_my_bloom_transaction_history() {
        // TODO: Implement logic for checking Bloom transaction history
    }

    @Then("I see an entry detailing the date, time, the name of the item and the items sell price with a negative sign")
    public void i_see_an_entry_detailing_the_date_time_the_name_of_the_item_and_the_items_sell_price_with_a_negative_sign() {
        // TODO: Ensure transaction history displays correct details
    }
}
