package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
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
import java.util.List;
import java.util.Objects;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private User friend;
    private List<User> friends;
    private Item item;
    private Image displayedImage;

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
        // Fills model with inventory items
        MvcResult inventoryResult = mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("imageItems"))
                .andReturn();

        // Extract imageItems from the model
        List<ImageItem> imageItems = (List<ImageItem>) inventoryResult.getModelAndView().getModel().get("imageItems");

        // Retrieve the item, cast to imageItem
        ImageItem imageItem = imageItems.get(0);

        // Get item image id
        Long itemId = imageItem.getId();

        // Use item post-mapping call
        mvcResult = mockMvc.perform(post("/inventory/use/" + itemId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/inventory"))
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
        // Refetch the current user
        currentUser = userService.getAuthenticatedUser();

        // Verify that the previous profile image ID is stored
        Assertions.assertNotNull(currentUser.getUploadedImageId(), "Previous profile image ID should be stored");

        // Verify that the previous image is different from the current one
        Assertions.assertNotEquals(currentUser.getImage().getId(), currentUser.getUploadedImageId(),
                "Previous profile image should differ from the current one");
    }

    //AC2 AC3
    @And("I have applied the {string} GIF item")
    public void iHaveAppliedTheGIFItem(String itemName) throws Exception {
        // Get item image id
        Long itemId = itemService.getItemByName(itemName).getId();

        // Use item post-mapping call
        mvcResult = mockMvc.perform(post("/inventory/use/" + itemId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/inventory"))
                .andReturn();
    }

    //AC2
    @And("I am friends with {string}")
    public void iAmFriendsWith(String friendEmail) {
        currentUser = userService.getAuthenticatedUser();
        User newUser = userService.getUserByEmail(friendEmail);

        currentUser.addFriend(newUser);
        newUser.addFriend(currentUser);
        userService.updateUserFriends(currentUser);
        userService.updateUserFriends(newUser);
    }

    //AC2
    @And("I views Liam's profile image on the {string} page")
    public void iViewsLaimsProfileImageOnTheEndpointPage(String endpoint) throws Exception {
        mvcResult = mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andReturn();
    }

    // AC2
    @Then("I can see the {string} GIF in place of {string}s old profile picture")
    public void iCanSeeTheGIFInPlaceOfSOldProfilePicture(String itemName, String userEmail) {
        //Because of the way images are retrieved checking by id is acceptable

        ImageItem expectedItem = (ImageItem) itemService.getItemByName(itemName);
        boolean expectedFriendIsShown = false;
        Long displayedImageId =  null;
        for (User friend : (List<User>) mvcResult.getModelAndView().getModel().get("friends")) {
            //Only retrieve the image from the correct user
            if (friend.getEmail().equals(userEmail)) {
                displayedImageId = friend.getImage().getId();
                expectedFriendIsShown = true;
            }
        }
        Assertions.assertTrue(expectedFriendIsShown, "True if the expected friend is displayed on the page");
        Assertions.assertEquals(expectedItem.getImage().getId(), displayedImageId, "The correct image is displayed");
    }

    //AC3
    @And("I am on my profile page")
    public void iAmOnMyProfilePage() throws Exception{
        mvcResult = mockMvc.perform(get("/view-user-profile"))
                .andExpect(status().isOk())
                .andReturn();
    }

    //AC3
    @When("I view my profile picture")
    public void iViewMyProfilePicture() {
        User displayedUser = (User) mvcResult.getModelAndView().getModel().get("user");
        displayedImage = displayedUser.getImage();
    }

    //AC3
    @Then("I can see the {string} GIF image as my profile picture")
    public void iCanSeeTheGIFImageAsMyProfilePicture(String itemName) {
        ImageItem expectedItem = (ImageItem) itemService.getItemByName(itemName);
        Assertions.assertEquals(expectedItem.getImage().getId(), displayedImage.getId(), "The gif items 'image' Id is the same Id as user in models image");
    }


    // AC4
    @Given("I have a friend {string} who has applied the {string} GIF image item")
    public void iHaveAFriendWhoHasAppliedTheGIFImageItem(String friendEmail, String imageItemName) throws Exception {
        // Get sarah user
        User sarah = userService.getUserByEmail(friendEmail);

        // Set friend from email
        this.friend = sarah;

        // Add the image item to the user's inventory
        Item itemCatTyping = itemService.getItemByName(imageItemName);

        sarah.addItem(itemCatTyping);

        // Save the adding item
        userService.saveUser(sarah);

        // Get the image items from sarah
        List<Item> sarahItems = sarah.getInventory();

        // Extract the image item (Only one item)
        ImageItem item = (ImageItem) sarahItems.get(0);

        // Apply image
        sarah.setImage(item.getImage());

        // Save the user
        userService.saveUser(sarah);
    }

    // AC4
    @When("I view their profile")
    public void iViewTheirProfile() throws Exception {
        // View profile is essentially going to manage friends page (AS LIAM)

        // Perform GET request to /manage-friends to load the friends list
        mvcResult = mockMvc.perform(get("/manage-friends"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friends"))
                .andReturn();

        // Set friend list from model
        // Extract friends from the model
        List<User> friends = (List<User>) mvcResult.getModelAndView().getModel().get("friends");

        // Set the friends list
        this.friends = friends;
    }

    // AC4
    @Then("I can see friend {string} with gif {string} displayed as their profile picture")
    public void iCanSeeFriendWithGifDisplayedAsTheirProfilePicture(String friendEmail, String imageItemName) {

        // Find the friend (sarah) from the specified email from the model list
        this.friend = friends.stream()
                .filter(f -> f.getEmail().equals(friendEmail))
                .findFirst()
                .orElse(null);

        // Get item, cast to imageItem
        ImageItem imageItem = (ImageItem) itemService.getItemByName(imageItemName);

        // Get item image id
        Long imageItemId = imageItem.getImage().getId();

        // Check that friends image ID matchs cat typing image id
        Assertions.assertEquals(imageItemId, friend.getImage().getId(),
                "Friend's profile picture should be set to the GIF item: " + imageItemName);
    }
}
