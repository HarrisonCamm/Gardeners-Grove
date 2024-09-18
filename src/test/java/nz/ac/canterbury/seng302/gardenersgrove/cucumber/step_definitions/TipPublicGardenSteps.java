package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private MockMvc mockMvc;
    private MvcResult mvcResult;
    private Garden testOwnedGarden;




    @Before
    public void setUp() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create a new unique location for each garden
        Location location = new Location("Test Street", "Test Suburb", "Test City", "1234", "Country");

        String gardenName = "Public Test Garden";
        testOwnedGarden = new Garden(gardenName, location, "100");

        // Mark the garden as public
        testOwnedGarden.setIsPublic(true);
    }

    @Given("I am on the garden details page for a garden I own for tips")
    public void i_am_on_the_garden_details_page_for_a_garden_i_own_for_tips() throws Exception {
        User loggedInUser = userService.getAuthenticatedUser();
        testOwnedGarden.setOwner(loggedInUser);
        gardenService.addGarden(testOwnedGarden);

        mvcResult = mockMvc.perform(get("/view-garden?gardenID=" + testOwnedGarden.getId()))
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
                () -> Assertions.assertEquals("Total Blooms tipped: " + totalBloomsTipped, totalBloomsTippedMessage)
        );
    }

    @Given("I have received tips for my garden for {int} blooms")
    public void i_have_received_tips_for_my_garden(Integer tipAmount) {
        testOwnedGarden.setTotalBloomTips(tipAmount);
        testOwnedGarden.setUnclaimedBlooms(tipAmount);
        //TODO once functionality implemented, use the controller to set tips
    }

    @Then("I see a claim blooms button to add the amount of unclaimed bloom tips of the garden to my balance")
    public void i_see_a_claim_blooms_button_to_add_the_amount_of_unclaimed_bloom_tips_of_the_garden_to_my_balance() throws UnsupportedEncodingException {
        boolean hasBloomsToClaim = (boolean) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("hasBloomsToClaim");
        String unclaimedBloomsMessage = (String) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("unclaimedBloomsMessage");

        Long gardenId = (Long) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("gardenID");
        Optional<Garden> garden = gardenService.findGarden(gardenId);
        Integer totalBloomsUnclaimed = garden.get().getUnclaimedBlooms();

        Integer actualBloomsUnclaimed = testOwnedGarden.getUnclaimedBlooms();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(garden),
                () -> Assertions.assertTrue(hasBloomsToClaim),
                () -> Assertions.assertEquals(actualBloomsUnclaimed, totalBloomsUnclaimed),
                () -> Assertions.assertEquals("You have " + actualBloomsUnclaimed + " Blooms to claim!", unclaimedBloomsMessage)
                );
    }
}
