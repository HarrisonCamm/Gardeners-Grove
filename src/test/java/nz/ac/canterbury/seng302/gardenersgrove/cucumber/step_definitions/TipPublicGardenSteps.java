package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TransactionService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class TipPublicGardenSteps {

    // Dependency Injection
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private GardenService gardenService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    TransactionService transactionService;

    // MockMvc for using API
    private MockMvc mockMvc;

    //Storing the result of the API call this will be used to get the model
    private MvcResult mvcResult;

    private MockHttpSession mockSession = new MockHttpSession();

    // Garden object to store the garden created in the background used to get the garden ID
    private Garden testCreatedGarden;

    // Variables to store the tip amount, old user bloom balance and old garden tip count
    private Integer tipAmount;
    private int oldUserBloomBalance;
    private int oldGardenTipCount;
    private long oldTransctionCount;


    @Before
    public void setUp() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // Background
    public void login_as_user(String email) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, "Password1!");
        var authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Background
    @Given("{string} has a public garden")
    public void has_a_public_garden(String userEmail) throws Exception {
        login_as_user(userEmail);

        Location location = new Location("Test Street", "Test Suburb", "Test City", "1234", "Country");


        // Create a garden and store it in inayasGarden
        mockMvc.perform(post("/create-garden")
                .param("name", "Test Garden")
                .param("location.streetAddress", location.getStreetAddress())
                .param("location.suburb", location.getSuburb())
                .param("location.city", location.getCity())
                .param("location.postcode", location.getPostcode())
                .param("location.country", location.getCountry())
                .param("size", "10")
                .with(csrf())); // Add CSRF token

        ArrayList<Garden> gardens = (ArrayList<Garden>) gardenService.getOwnedGardens(userService.getAuthenticatedUser().getUserId());
        testCreatedGarden = gardens.get(gardens.size() - 1);

        // Make garden public
        mockMvc.perform(patch("/view-garden")
                .param("gardenID", testCreatedGarden.getId().toString())
                .param("isPublic", "true")
                .with(csrf()));
    }

    // Background
    @And("{string} has {int} Blooms")
    public void has_blooms(String email, int bloomAmount) {

        login_as_user(email);

        User user = userService.getUserByEmail(email);
        user.setBloomBalance(bloomAmount);

        // Set password to avoid authentication issues
        user.setPassword("Password1!");

        // Resave user with new balance
        userService.addUser(user);

        SecurityContextHolder.clearContext();
    }

    // Background
    @And("{string}'s garden has been tipped {int} blooms by {string}")
    public void s_garden_has_been_tipped_blooms_by(String ownerEmail, Integer tipAmount, String senderEmail) throws Exception {
        //NOTE: unused variable as it needs to be 'reusable' and read in English by PO

        login_as_user(senderEmail);

        mockMvc.perform(post("/tip-blooms")
                .param("gardenID", testCreatedGarden.getId().toString())
                .param("tipAmount", tipAmount.toString()));

        // Log out
        SecurityContextHolder.clearContext();
    }


    // AC3, AC4, AC5
    @And("I am on the garden details page for a garden I do not own")
    public void i_am_on_the_garden_details_page_for_a_garden_i_do_not_own() throws Exception {
        mvcResult = mockMvc.perform(get("/view-garden?gardenID=" + testCreatedGarden.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("viewUnownedGardenDetailsTemplate"))
                .andReturn();
    }

    // AC3, AC4
    @When("I enter an invalid tip {int}")
    public void i_enter_an_invalid_tip(Integer tipAmount) {
        this.tipAmount = tipAmount;
    }

    // AC3, AC4
    @Then("I am shown an error message {string}")
    public void i_am_shown_an_error_message(String errorMessage) {
        assertEquals(mvcResult.getModelAndView().getModel().get("tipAmountError"), errorMessage);
    }

    // AC5
    @When("I enter an valid tip {int}")
    public void i_enter_an_valid_tip(Integer tipAmount) {
        this.tipAmount = tipAmount;
    }

    // AC5
    @When("I enter a valid tip that is my entire balance")
    public void i_enter_a_valid_tip_that_is_my_entire_balance() {
        this.tipAmount = userService.getAuthenticatedUser().getBloomBalance();
    }

    // AC3, AC4, AC5
    @And("I confirm the transaction by clicking Confirm")
    public void i_confirm_the_transaction_for_by_clicking() throws Exception {

        // Store old user bloom balance and old garden tip count for later
        oldUserBloomBalance = userService.getAuthenticatedUser().getBloomBalance();
        oldGardenTipCount = gardenService.findGarden(testCreatedGarden.getId()).get().getTotalBloomTips();

        // Perform the post request and store the ResultActions
        ResultActions postResultActions = mockMvc.perform(post("/tip-blooms")
                .param("gardenID", testCreatedGarden.getId().toString())
                .param("tipAmount", tipAmount.toString())
                .with(csrf()));

        // Extract the HttpSession from the ResultActions
        HttpSession session = postResultActions.andReturn().getRequest().getSession();

        // Perform the get request with the extracted HttpSession
        assert session != null;
        mvcResult = mockMvc.perform(get("/view-garden?gardenID=" + testCreatedGarden.getId())
                        .session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(view().name("viewUnownedGardenDetailsTemplate"))
                .andReturn();
    }

    //AC5
    @Then("the Blooms are deducted from my account")
    public void the_blooms_are_deducted_from_my_account() {
        assertEquals(Math.max(oldUserBloomBalance - tipAmount, 0), (int) userService.getAuthenticatedUser().getBloomBalance());
    }

    //AC5
    @Then("the garden's tip count is updated")
    public void the_garden_s_tip_count_is_updated() {
        // It will be unclaimed blooms not claimed blooms
        Integer blooms = gardenService.findGarden(testCreatedGarden.getId()).orElseThrow(
                () -> new IllegalArgumentException("Garden not found")
        ).getTotalBloomTips();

        assertEquals(oldGardenTipCount + tipAmount, blooms);
    }

    // AC6, AC7
    @Given("I am on the garden details page for a garden I own for tips")
    public void i_am_on_the_garden_details_page_for_a_garden_i_own_for_tips() throws Exception {

        mvcResult = mockMvc.perform(get("/view-garden?gardenID=" + testCreatedGarden.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("viewGardenDetailsTemplate"))
                .andReturn();
    }

    // AC6
    @Then("I can see the total number of Blooms the garden has received as tips")
    public void i_can_see_the_total_number_of_blooms_the_garden_has_received_as_tips() {
        String totalBloomsTippedMessage = (String) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("totalBloomsTippedMessage");

        Long gardenId = (Long) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("gardenID");
        Optional<Garden> garden = gardenService.findGarden(gardenId);

        Integer totalBloomsTipped = garden.get().getTotalBloomTips();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(garden),
                () -> assertEquals("Total Blooms tipped: " + totalBloomsTipped, totalBloomsTippedMessage)
        );
    }

    //AC7
    @Given("I have received tips for my garden")
    public void i_have_received_tips_for_my_garden() {
        Integer tipAmount = gardenService.findGarden(testCreatedGarden.getId()).get().getUnclaimedBlooms();
        assertTrue(tipAmount > 0);
    }

    //AC7
    @Then("I see a claim blooms button to add the amount of unclaimed bloom tips of the garden to my balance")
    public void i_see_a_claim_blooms_button_to_add_the_amount_of_unclaimed_bloom_tips_of_the_garden_to_my_balance() throws Exception {
        mvcResult = mockMvc.perform(get("/view-garden?gardenID=" + testCreatedGarden.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("viewGardenDetailsTemplate"))
                .andReturn();

        // Get values from the model
        String claimBloomsButtonText = (String) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("claimBloomsButtonText");

        // Get actual amount that was changed in the controller
        Integer actualBloomsUnclaimed =  gardenService.findGarden(testCreatedGarden.getId()).get().getUnclaimedBlooms();

        Assertions.assertEquals("Claim " + actualBloomsUnclaimed + " Blooms" , claimBloomsButtonText, "The text on the button should be correct");
    }

    //AC8
    @And("I choose to claim the Blooms from my garden's tips")
    public void i_choose_to_claim_the_blooms_from_my_gardens_tips() throws Exception{
        oldUserBloomBalance = userService.getAuthenticatedUser().getBloomBalance();
        oldTransctionCount = transactionService.findTransactionsByUser(userService.getAuthenticatedUser(), 0, 10).getTotalElements();
        mvcResult = mockMvc.perform(post("/claim-tips")
                .param("gardenID", testCreatedGarden.getId().toString())
                .with(csrf())
                .session(mockSession))
                .andExpect(status().is3xxRedirection())
                .andReturn();
    }

    @When("I confirm the action")
    public void i_confirm_the_action() throws Exception{
        mvcResult = mockMvc.perform(get("/view-garden")
                .param("gardenID", testCreatedGarden.getId().toString())
                .with(csrf())
                .session(mockSession))
                .andExpect(status().isOk())
                .andReturn();
    }


    //AC8
    @Then("the {int} blooms are added to my account")
    public void the_blooms_are_added_to_my_account(int amountClaimed) {
        tipAmount = amountClaimed;
        assertEquals(oldUserBloomBalance + amountClaimed, (int) userService.getAuthenticatedUser().getBloomBalance());
    }

    //AC8
    @And("a transaction is added to my account history")
    public void a_transaction_is_added_to_my_account_history() {
        Long transactionCount = transactionService.findTransactionsByUser(userService.getAuthenticatedUser(), 0, 10).getTotalElements();
        assertEquals(oldTransctionCount + 1, transactionCount);
    }
    //AC8
    @And("a confirmation message is displayed")
    public void a_confirmation_message_is_displayed() {
        String actualMessage = (String) mvcResult.getModelAndView().getModel().get("claimedTipsMessage");
        String expectedMessage = "You have claimed " + tipAmount + " Blooms! \uD83C\uDF31";
        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    //AC8
    @And("the total number of Blooms I can claim is {int}")
    public void the_total_number_of_blooms_i_can_claim_is(int expectedUnclaimedBlooms) {
        int totalUnclaimedTips = transactionService.totalUnclaimedTips(testCreatedGarden);
        assertEquals(expectedUnclaimedBlooms, totalUnclaimedTips);
    }
}
