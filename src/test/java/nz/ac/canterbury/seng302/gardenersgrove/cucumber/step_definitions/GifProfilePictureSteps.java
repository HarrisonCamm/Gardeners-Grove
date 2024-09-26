package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.ImageItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.ItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class GifProfilePictureSteps {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    private MockMvc mockMvc;
    private MvcResult mvcResult;
    private User currentUser;
    private Item item;

    @Before
    public void setUp() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // Background Step
    @And("I have a inventory with {string} GIF profile item")
    public void i_have_a_inventory_with_imageItem(String string) {
        // Get the current user
        User loggedInUser = userService.getAuthenticatedUser();

        // Set the current user
        currentUser = loggedInUser;

        // Add the image item to the user's inventory
        loggedInUser.addItem(itemService.getItemByName(string));

        // Set the image item
        item = itemService.getItemByName(string);

        // Save the user
        userService.saveUser(loggedInUser);
    }

    // AC1
    @When("I click on the {string} button for the {string} imageItem")
    public void iClickOnTheButtonForTheImageItem(String buttonName, String imageItemName) throws Exception {
        // UPDATE TO GET FROM INVENTORY MODEL?

        // Retrieve the item, cast to imageItem
        ImageItem imageItem = (ImageItem) item;

        // Get item image id
        Long itemImageId = imageItem.getImage().getId();

        // Use item post-mapping call
        mvcResult = mockMvc.perform(post("/inventory/use/" + itemImageId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("inventoryTemplate"))
                .andReturn();
    }

    // AC1
    @Then("the {string} gif replaces my current profile picture")
    public void theGifReplacesMyCurrentProfilePicture(String expectedImageName) {
        // Verify that the current profile picture matches the expected image
        Assertions.assertNotNull(currentUser.getImage(), "Current user should have a profile image");
        Assertions.assertEquals(expectedImageName, item.getName(),
                "Profile picture should be updated to the selected GIF");
    }

    // AC1
    @And("my old profile picture is stored for later")
    public void myOldProfilePictureIsStoredForLater() {
        // Verify that the previous profile image ID is stored
        Assertions.assertNotNull(currentUser.getPreviousImageId(), "Previous profile image ID should be stored");

        // Verify that the previous image is different from the current one
        Assertions.assertNotEquals(currentUser.getImage().getId(), currentUser.getPreviousImageId(),
                "Previous profile image should differ from the current one");
    }
}
