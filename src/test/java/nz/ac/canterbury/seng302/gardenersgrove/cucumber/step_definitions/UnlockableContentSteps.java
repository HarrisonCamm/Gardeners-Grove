package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.InventoryItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TransactionService;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings({"unchecked", "SpringJavaInjectionPointsAutowiringInspection"})
public class UnlockableContentSteps {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private InventoryItemService inventoryService;
    @Autowired
    private TransactionService transactionService;
    private ResultActions resultActions;
    private MvcResult mvcResult;
    private MockMvc mockMvc;
    private User currentUser;
    private Item item;
    private List<Map.Entry<Item,Integer>> ownedItems;
    private int oldBloomBalance;
    private Transaction transaction;

    @Before
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @When("I click Inventory")
    public void i_click_inventory() throws Exception {
        this.mvcResult = mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("imageItems"))
                .andExpect(model().attributeExists("badgeItems"))
                .andReturn();
    }

    // AC1
    @Then("I am shown my inventory of items")
    public void i_am_shown_my_inventory_of_items() {
        // Retrieve items from the model
        List<Map.Entry<Item,Integer>> badgeItems = (List<Map.Entry<Item,Integer>>) mvcResult.getModelAndView().getModel().get("badgeItems");
        List<Map.Entry<Item,Integer>> imageItems = (List<Map.Entry<Item,Integer>>) mvcResult.getModelAndView().getModel().get("imageItems");

        // Retrieve the current user
        currentUser = userService.getAuthenticatedUser();

        List<Map.Entry<Item,Integer>> expectedOwnedItems = inventoryService.getItems(currentUser);

        List<Map.Entry<Item,Integer>> expectedOwnedBadgeItems = new ArrayList<>();
        List<Map.Entry<Item,Integer>> expectedOwnedImageItems = new ArrayList<>();

        for (Map.Entry<Item,Integer> expectedItem: expectedOwnedItems) {
            if (expectedItem.getKey() instanceof BadgeItem) {
                expectedOwnedBadgeItems.add(expectedItem);
            }
            if (expectedItem.getKey() instanceof ImageItem) {
                expectedOwnedImageItems.add(expectedItem);
            }
        }


        // Assertions to verify the inventory
        Assertions.assertEquals(expectedOwnedBadgeItems.size(), badgeItems.size(), "Badge items count does not match.");
        Assertions.assertTrue(badgeItems.containsAll(expectedOwnedBadgeItems), "Badge items do not match the expected owned items.");

        Assertions.assertEquals(expectedOwnedImageItems.size(), imageItems.size(), "Image items count does not match.");
        Assertions.assertTrue(imageItems.containsAll(expectedOwnedImageItems), "Image items do not match the expected owned items.");
    }

    // AC2
    @When("I click Shop")
    public void i_click_shop() throws Exception {
        this.mvcResult = mockMvc.perform(get("/shop"))
                .andExpect(status().isOk())
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
        currentUser = userService.getAuthenticatedUser();
        ownedItems = inventoryService.getItems(currentUser);
        oldBloomBalance = currentUser.getBloomBalance();
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
                        (Executable) () -> Assertions.assertNotNull(badgeItem.getIcon(), "BadgeItem icon should not be null")
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
    public void i_am_in_my_inventory() throws Exception {
        // Access the inventory page to ensure the user is in their inventory
        mvcResult = mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventoryTemplate"))
                .andReturn();
    }

    @Then("I can see a list of my items I have purchased that have a picture, name and quantity")
    public void i_can_see_a_list_of_my_items_i_have_purchased_that_have_a_picture_name_and_quantity() {
        // TODO: Implement logic for displaying purchased items
    }

//    AC 5
    @When("I attempt to buy an item costing more than my current Blooms balance")
    public void i_attempt_to_buy_an_item_costing_more_than_my_current_blooms_balance() throws Exception {

        item = itemService.getAllItems().iterator().next();
        item.setPrice(2000);
        itemService.saveItem(item);

        // Perform the POST request
        mvcResult = mockMvc.perform(post("/shop")
                        .param("itemId", item.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();
    }
//AC 5
    @Then("I am shown the error message {string}")
    public void i_am_shown_the_error_message(String expectedMessage) {

        String message = (String) mvcResult.getModelAndView().getModel().get("purchaseNotSuccessful");

        assertEquals(expectedMessage, message);

    }
//AC 5
    @And("the item is not added to my items")
    public void the_item_is_not_added_to_my_items() {

        List<Map.Entry<Item,Integer>> newOwnedItems = inventoryService.getItems(currentUser);
        assertSame(ownedItems.size(), newOwnedItems.size());

    }

//    AC 6
    @When("I buy an item costing less than or equal to my current Blooms balance")
    public void i_buy_an_item_costing_less_than_or_equal_to_my_current_blooms_balance() throws Exception {
        item = itemService.getAllItems().iterator().next();
        item.setPrice(10);
        itemService.saveItem(item);

        mvcResult = mockMvc.perform(post("/shop")
                        .param("itemId", item.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();
    }

//    AC 6
    @Then("that item is added to my inventory")
    public void that_item_is_added_to_my_inventory() {
        currentUser = userService.getAuthenticatedUser();
        List<Map.Entry<Item,Integer>> newOwnedItems = inventoryService.getItems(currentUser);

        assertNotSame(newOwnedItems, ownedItems);
        IntStream.range(0, newOwnedItems.size())
                .forEach(i -> {
                    Item ownedItem = newOwnedItems.get(i).getKey();
                    boolean containsItem = ownedItem.getName().equals(item.getName());
                    Assertions.assertTrue(containsItem);
                });
    }

//    AC 6
    @And("the items cost in Blooms is deducted from my account")
    public void the_items_cost_in_blooms_is_deducted_from_my_account() {

        int itemPrice = item.getPrice();
        int newBloomBalance = currentUser.getBloomBalance();
        assertEquals(oldBloomBalance - itemPrice, newBloomBalance);

    }

//    AC 6
    @And("I am shown a confirmation message {string}")
    public void i_am_shown_a_confirmation_message(String expectedMessage) {
        String message = (String) mvcResult.getModelAndView().getModel().get("purchaseSuccessful");
        assertEquals(expectedMessage, message);
    }

//    AC 7
    @Given("I have more than one of the same item")
    public void i_have_more_than_one_of_the_same_item() throws Exception {
        item = itemService.getItemByName("Cat Typing");
        item.setPrice(10);
        itemService.saveItem(item);
        // Perform the POST request
        mvcResult = mockMvc.perform(post("/shop")
                        .param("itemId", item.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        // Perform the POST request
        mvcResult = mockMvc.perform(post("/shop")
                        .param("itemId", item.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();

    }

//    AC 7
    @When("I view my inventory")
    public void i_view_my_inventory() throws Exception {
        i_click_inventory();
    }

//    AC 7
    @Then("the quantity is displayed alongside the item rather than displaying multiple instances of the item")
    public void the_quantity_is_displayed_alongside_the_item_rather_than_displaying_multiple_instances_of_the_item() {
        // TODO: Ensure the quantity is displayed properly
        List<Map.Entry<ImageItem,Integer>> imageItems = (List<Map.Entry<ImageItem, Integer>>) mvcResult.getModelAndView().getModel().get("imageItems");
        imageItems.forEach(eachItem -> {
            Item imageItem = eachItem.getKey();
            Integer imageItemQuantity = eachItem.getValue();
            if (imageItem.getName().equals(item.getName())) {
                assertTrue(imageItemQuantity > 1);
            }
        });
    }

    // AC8
    @When("I click on an item")
    public void i_click_on_an_item() throws Exception {
        i_buy_an_item_costing_less_than_or_equal_to_my_current_blooms_balance();
        resultActions = mockMvc.perform(get("/item?itemID=" + item.getId()));
    }


    // AC8
    @Then("I am taken to a page for that item which displays more information on the item including picture, name, original price, and resale price")
    public void i_am_taken_to_a_page_for_that_item_which_displays_more_information_on_the_item_including_picture_name_description_original_price_and_resale_price() throws Exception {
        mvcResult = resultActions.andExpect(status().isOk())
                .andExpect(view().name("itemDetailsTemplate"))
                .andReturn();

        Item item = (Item) mvcResult.getModelAndView().getModel().get("item");
        String originalPriceText = (String) mvcResult.getModelAndView().getModel().get("originalPriceText");
        String resalePriceText = (String) mvcResult.getModelAndView().getModel().get("resalePriceText");

        Assertions.assertAll(
                () -> assertNotNull(item),
                () -> assertNotNull(originalPriceText),
                () -> assertNotNull(resalePriceText)
        );
    }

    // AC9
    @Given("I am viewing an item in my inventory")
    public void i_am_viewing_an_item_in_my_inventory() {
        // TODO: Setup context for viewing an item in inventory
    }

    // AC9
    @When("I click the Sell button for that item")
    public void i_click_the_sell_button_for_that_item() {
        // TODO: Implement logic for selling an item
    }

    // AC9
    @Then("a confirmation popup with a cancel button and confirm button is shown with the message {string}")
    public void a_confirmation_popup_with_a_cancel_button_and_confirm_button_is_shown_with_the_message(String arg0) {
        // TODO: Display confirmation popup for selling an item
    }

//    AC 10
    @Given("I purchase an item")
    public void i_purchase_an_item() throws Exception {
        i_buy_an_item_costing_less_than_or_equal_to_my_current_blooms_balance();
        currentUser = userService.getAuthenticatedUser();
    }

//    AC 10
    @When("I check my Bloom transaction history")
    public void i_check_my_bloom_transaction_history() {
        List<Transaction> currentUsersTransactions = transactionService.getTransactionsBySender(currentUser);
        transaction = currentUsersTransactions.get(currentUsersTransactions.size() - 1);
    }

//    AC 10
    @Then("I see an entry detailing the date, time, the name of the item and the items sell price with a negative sign")
    public void i_see_an_entry_detailing_the_date_time_the_name_of_the_item_and_the_items_sell_price_with_a_negative_sign() {
        Date expectedDate = new Date();
        User expectedSender = currentUser;
        User expectedReceiver = userService.getUserByEmail("gardenersgrove@email.com");
        Integer expectedAmount = item.getPrice();
        String expectedType = "Shop Purchase";
        assertEquals(expectedDate.getDate(), transaction.getTransactionDate().getDate());
        assertTrue(userService.areUsersEqual(expectedSender, transaction.getSender()));
        assertTrue(userService.areUsersEqual(expectedReceiver, transaction.getReceiver()));
        assertEquals(expectedAmount, transaction.getAmount());
        assertEquals(expectedType, transaction.getTransactionType());
    }


}
