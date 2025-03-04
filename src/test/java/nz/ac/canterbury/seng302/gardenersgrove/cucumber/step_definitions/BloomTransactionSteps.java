package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Transaction;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TransactionService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
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
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class BloomTransactionSteps {

    private static final Integer PAGE_SIZE = 10;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserProfileController userProfileController;

    @Autowired
    private ResourceLoader resourceLoader;

    private static MockMvc mockMvcUserProfile;

    private MockMvc mockMvc;
    private MvcResult mvcResult;
    private ResultActions resultActions;

    private User currentUser;

    private User gardenersGroveUser;

    private static Transaction transaction1;


    private static Transaction transaction;

    @BeforeAll
    public static void globalSetup() {

    }


    @Before
    public void setUp() throws IOException {
        userProfileController = new UserProfileController(userService, userRepository, imageService, transactionService, itemService);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvcUserProfile = MockMvcBuilders.standaloneSetup(userProfileController).build();

        Path path = Paths.get(resourceLoader.getResource("classpath:static/images/defaultUserImage.png").getURI());
        byte[] imageBytes = Files.readAllBytes(path);

        Image image = new Image(imageBytes, "png", false);
        gardenersGroveUser = new User("Gardeners Grove", "Inc", false, "gardenersgrove@email.com", "Password1!", "", image);

    }



    //AC1
    @When("I navigate to any page {string} in the system")
    public void i_navigate_to_any_page_in_the_system(String endpoint) throws Exception {
        mvcResult = mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andReturn();
    }

    //AC1, AC2
    @Then("I can see my Bloom balance displayed prominently in the header or a dedicated section")
    public void i_can_see_my_bloom_balance_displayed_prominently_in_the_header_or_a_dedicated_section() throws Exception {

        currentUser = userService.getAuthenticatedUser();
        Integer balance = currentUser.getBloomBalance();

        String content = mvcResult.getResponse().getContentAsString();
        boolean hasBloomBalance = content.contains("<div class=\"balanceDisplay\"")
                && content.contains("<span class=\"navBar-bloom-display\" id=\"bloomBalanceNavbar\">" + balance.toString());

        Assertions.assertNotNull(currentUser.getBloomBalance(), "Expected bloom balance to be a number, but it was null");

        Assertions.assertTrue(hasBloomBalance, "Expected to find a bloom balance icon and number on the page.");
    }



    //AC2
    @When("I navigate to my profile page")
    public void i_navigate_to_my_profile_page() throws Exception {
        this.mvcResult = mockMvc.perform(get("/view-user-profile")).andExpect(status()
                .isOk())
                .andExpect(view().name("viewUserProfileTemplate"))
                .andReturn();
    }

    //AC2
    @Then("I can see a detailed transaction history for the Bloom currency")
    public void i_can_see_a_detailed_transaction_history_for_the_bloom_currency() {
        Assertions.assertNotNull(mvcResult.getModelAndView().getModel().get("transactions"));
    }

    //AC2
    @Then("the transaction history should be paginated or scrollable if it exceeds a certain number of entries")
    public void the_transaction_history_should_be_paginated_or_scrollable_if_it_exceeds_a_certain_number_of_entries() {
        Object transactions = mvcResult.getModelAndView().getModel().get("transactions");
        Integer transactionCount = ((List<?>) transactions).size();
        Integer totalPages = (Integer) mvcResult.getModelAndView().getModel().get("totalPages");
        Integer pageSize = (Integer) mvcResult.getModelAndView().getModel().get("pageSize");

        Assertions.assertEquals(PAGE_SIZE, pageSize);
        if(transactionCount > PAGE_SIZE) {
            Assertions.assertEquals((int) Math.floor(transactionCount / PAGE_SIZE)+1, totalPages);
        }
        else {
            Assertions.assertEquals(0, totalPages);
        }
    }

    //AC3
    @Given("I am a new user or have not made any transactions")
    public void i_am_a_new_user_or_have_not_made_any_transactions() throws Exception {

        //probably shouldn't be here, but isn't working otherwise.
        this.mvcResult = mockMvcUserProfile.perform(get("/view-user-profile")).andExpect(status()
                        .isOk())
                .andExpect(view().name("viewUserProfileTemplate"))
                .andReturn();

        Object transactions = mvcResult.getModelAndView().getModel().get("transactions");
        Integer transactionCount = ((List<?>) transactions).size();
        Assertions.assertEquals(0, transactionCount);
    }

    //AC3
    @Then("I should see a message indicating that no transaction history is available")
    public void i_should_see_a_message_indicating_that_no_transaction_history_is_available() {
        Object noTransactionsMessage = mvcResult.getModelAndView().getModel().get("noTransactionsText");
        Assertions.assertEquals("No Transactions to Display", noTransactionsMessage);
    }

    //AC3
    @Then("I should see a brief description of how to earn or spend Blooms")
    public void i_should_see_a_brief_description_of_how_to_earn_or_spend_blooms() {
        Object earnBloomsText = mvcResult.getModelAndView().getModel().get("earnBloomsText");
        Assertions.assertEquals("You can earn Blooms by: Selling plants, playing games, receiving tips from other users", earnBloomsText);

        Object noTransactionsText = mvcResult.getModelAndView().getModel().get("spendBloomsText");
        Assertions.assertEquals("You can spend Blooms by: Tipping other people's gardens, playing games, buying plants for your gardens", noTransactionsText);

    }

    //AC3
    @Given("I am viewing the transaction history on my profile page")
    public void i_am_viewing_the_transaction_history_on_my_profile_page() throws Exception {
        this.mvcResult = mockMvcUserProfile.perform(get("/view-user-profile")).andExpect(status()
                        .isOk())
                .andExpect(view().name("viewUserProfileTemplate"))
                .andReturn();

        Object transactions = Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("transactions");
        int transactionCount = ((List<?>) transactions).size();
        Assertions.assertTrue(transactionCount > 0);
    }

    //AC3
    @When("I click on a specific transaction")
    public void i_click_on_a_specific_transaction() {

        @SuppressWarnings("unchecked")
        List<Transaction> transactions = (List<Transaction>) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("transactions");

        Assertions.assertNotNull(transactions.get(0));

        transaction1 = transactions.get(0);
    }

    //AC3
    @Then("I can see additional details for that transaction, if available")
    public void i_can_see_additional_details_for_that_transaction_if_available() {
        Assertions.assertEquals(transaction.getTransactionId(),transaction1.getTransactionId());
    }


    //AC3
    @Given("there are existing transactions")
    public void there_are_existing_transactions() {
        User currentUser = userService.getAuthenticatedUser();
        transaction = transactionService.addTransaction(100, "blooms from Gardener's Grove.","reward", currentUser.getUserId(), gardenersGroveUser.getUserId());
    }
}
