package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Then("I am shown my inventory of items")
    public void i_am_shown_my_inventory_of_items() {
        List<String[]> badgeItems = (List<String[]>) mvcResult.getModelAndView().getModel().get("badgeItems");
//        List<String[]> ownedBadgeItems = currentUser.getBadgeItems(); todo
        List<String[]> ownedBadgeItems = new ArrayList<>();
        List<String[]> gifItems = (List<String[]>) mvcResult.getModelAndView().getModel().get("gifItems");
//        List<String[]> ownedGifItems = currentUser.getBadgeItems(); todo
        List<String[]> ownedGifItems = new ArrayList<>();
        Assertions.assertEquals(ownedBadgeItems, badgeItems);
        Assertions.assertEquals(ownedGifItems, gifItems);

    }

    @When("I click Shop")
    public void i_click_shop() {
        // TODO: Implement logic for clicking the shop
    }

    @Then("I am shown the shop")
    public void i_am_shown_the_shop() {
        // TODO: Implement logic for displaying the shop
    }

    @Given("I am in the shop")
    public void i_am_in_the_shop() {
        // TODO: Setup initial context for being in the shop
    }

    @Then("I can see a list of items for sale with a picture, name, description and price in Blooms")
    public void i_can_see_a_list_of_items_for_sale_with_a_picture_name_description_and_price_in_blooms() {
        // TODO: Implement logic for displaying list of items for sale
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
        // TODO: Implement logic for attempting to buy an expensive item
    }

    @Then("I am shown the error message {string}")
    public void i_am_shown_the_error_message(String arg0) {
        // TODO: Implement logic for displaying an error message
    }

    @And("the item is not added to my items")
    public void the_item_is_not_added_to_my_items() {
        // TODO: Ensure item is not added to inventory on failure
    }

    @When("I buy an item costing less than or equal to my current Blooms balance")
    public void i_buy_an_item_costing_less_than_or_equal_to_my_current_blooms_balance() {
        // TODO: Implement logic for buying an affordable item
    }

    @Then("that item is added to my inventory")
    public void that_item_is_added_to_my_inventory() {
        // TODO: Add the purchased item to inventory
    }

    @And("the items cost in Blooms is deducted from my account")
    public void the_items_cost_in_blooms_is_deducted_from_my_account() {
        // TODO: Deduct the item cost from the user's Blooms account
    }

    @And("I am shown a confirmation message {string}")
    public void i_am_shown_a_confirmation_message(String arg0) {
        // TODO: Display a confirmation message after purchase
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
