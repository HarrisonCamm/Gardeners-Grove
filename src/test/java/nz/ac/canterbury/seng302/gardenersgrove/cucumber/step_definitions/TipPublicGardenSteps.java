package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class TipPublicGardenSteps {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private GardenService gardenService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    private MockMvc mockMvc;
    private MvcResult mvcResult;
    private Garden inayasGarden;

    private ResultActions resultActions;




    @Before
    public void setUp() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    public void logInAsUser(String email) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, "Password1!");
        var authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Given("{string} has a public garden")
    public void has_a_public_garden(String userEmail) throws Exception {
        logInAsUser(userEmail);

        Location location = new Location("Test Street", "Test Suburb", "Test City", "1234", "Country");


        resultActions = mockMvc.perform(post("/create-garden")
                .param("name", "Test Garden 1")
                .param("location.streetAddress", location.getStreetAddress())
                .param("location.suburb", location.getSuburb())
                .param("location.city", location.getCity())
                .param("location.postcode", location.getPostcode())
                .param("location.country", location.getCountry())
                .param("size", "10")
                .with(csrf())); // Add CSRF token

        ArrayList<Garden> gardens = (ArrayList<Garden>) gardenService.getOwnedGardens(userService.getAuthenticatedUser().getUserId());
        inayasGarden = gardens.get(gardens.size() - 1);

        mockMvc.perform(patch("/view-garden")
                .param("gardenID", inayasGarden.getId().toString())
                .param("isPublic", "true")
                .with(csrf()));

        // Log out
        SecurityContextHolder.clearContext();
    }

    @And("{string}'s garden has been tipped {int} blooms by {string}")
    public void s_garden_has_been_tipped_blooms_by(String ownerEmail, Integer tipAmount, String senderEmail) throws Exception {
        logInAsUser(senderEmail);

        mockMvc.perform(post("/tip-blooms")
                .param("gardenID", inayasGarden.getId().toString())
                .param("tipAmount", tipAmount.toString()));

        // Log out
        SecurityContextHolder.clearContext();
    }


    @Given("I am on the garden details page for a garden I own for tips")
    public void i_am_on_the_garden_details_page_for_a_garden_i_own_for_tips() throws Exception {

        mvcResult = mockMvc.perform(get("/view-garden?gardenID=" + inayasGarden.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("viewGardenDetailsTemplate"))
                .andReturn();
    }

    @Then("I can see the total number of Blooms the garden has received as tips")
    public void i_can_see_the_total_number_of_blooms_the_garden_has_received_as_tips() throws UnsupportedEncodingException {
        String totalBloomsTippedMessage = (String) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("totalBloomsTippedMessage");

        Long gardenId = (Long) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("gardenID");
        Optional<Garden> garden = gardenService.findGarden(gardenId);

        Integer totalBloomsTipped = garden.get().getTotalBloomTips();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(garden),
                () -> assertEquals("Total Blooms tipped: " + totalBloomsTipped, totalBloomsTippedMessage)
        );
    }

    @Given("I have received tips for my garden for {int} blooms")
    public void i_have_received_tips_for_my_garden(Integer tipAmount) {
        inayasGarden.setTotalBloomTips(tipAmount);
        inayasGarden.setUnclaimedBlooms(tipAmount);
        //TODO once functionality implemented, use the controller to set tips
    }

    @Then("I see a claim blooms button to add the amount of unclaimed bloom tips of the garden to my balance")
    public void i_see_a_claim_blooms_button_to_add_the_amount_of_unclaimed_bloom_tips_of_the_garden_to_my_balance() throws Exception {
        mvcResult = mockMvc.perform(get("/view-garden?gardenID=" + inayasGarden.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("viewGardenDetailsTemplate"))
                .andReturn();

        Map model =  mvcResult.getModelAndView().getModel();
        boolean hasBloomsToClaim = (boolean) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("hasBloomsToClaim");
        String unclaimedBloomsMessage = (String) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("unclaimedBloomsMessage");

        Long gardenId = (Long) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("gardenID");
        Optional<Garden> garden = gardenService.findGarden(gardenId);
        Integer totalBloomsUnclaimed = garden.get().getUnclaimedBlooms();

        Integer actualBloomsUnclaimed = inayasGarden.getUnclaimedBlooms();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(garden),
                () -> Assertions.assertTrue(hasBloomsToClaim),
                () -> assertEquals(actualBloomsUnclaimed, totalBloomsUnclaimed),
                () -> assertEquals("You have " + actualBloomsUnclaimed + " Blooms to claim!", unclaimedBloomsMessage)
                );
    }

    @And("I am on the garden details page for a garden I do not own")
    public void i_am_on_the_garden_details_page_for_a_garden_i_do_not_own() throws Exception {
        mvcResult = mockMvc.perform(get("/view-garden?gardenID=" + inayasGarden.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("viewUnownedGardenDetailsTemplate"))
                .andReturn();
    }

    @When("I enter an invalid tip {int}")
    public void i_enter_an_invalid_tip(Integer tipAmount) throws Exception {
        mockMvc.perform(post("/tip-blooms")
                .param("gardenID", inayasGarden.getId().toString())
                .param("tipAmount", tipAmount.toString()));
    }

    @Then("I am shown an error message {string}")
    public void i_am_shown_an_error_message(String errorMessage) {
        // Write code here that turns the phrase above into concrete actions
        assertEquals(mvcResult.getModelAndView().getModel().get("tipAmountError"), errorMessage);
    }

    @When("I enter an invalid tip {string}")
    public void i_enter_an_invalid_tip(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I enter an valid tip {int}")
    public void i_enter_an_valid_tip(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I confirm the transaction for {int} by clicking {string}")
    public void i_confirm_the_transaction_for_by_clicking(Integer int1, String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the Blooms are deducted from my account")
    public void the_blooms_are_deducted_from_my_account() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the garden's tip count is updated")
    public void the_garden_s_tip_count_is_updated() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I enter an valid tip {string}")
    public void i_enter_an_valid_tip(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I confirm the transaction for {string} by clicking {string}")
    public void i_confirm_the_transaction_for_by_clicking(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


}
