package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.FriendRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
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
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")


public class CancelFriendRequestsSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    UserService userService;

    @Autowired
    GardenService gardenService;

    private MockMvc mockMvc;
    private User otherUser;

    private User friendToCancel;

    private MvcResult mvcResult; //grayed out but accessed

    private User currentUser;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @And("{string} has not yet accepted or declined the invite")
    public void userHasNotYetAcceptedTheInvite(String otherUserEmail) {
        otherUser = userService.getUserByEmail(otherUserEmail);
        currentUser = userService.getAuthenticatedUser();

        List<FriendRequest> otherUserFriendRequests = userService.getPendingFriendRequests(otherUser);
        Assertions.assertNotNull(otherUserFriendRequests);
        Assertions.assertTrue(otherUserFriendRequests.stream().anyMatch(request -> request.getStatus().equals("Pending")));
    }

    @When("I cancel my friend request")
    public void iCancelMyFriendRequest() throws Exception {
        mockMvc.perform(post("/manage-friends")
                        .param("action", "cancel")
                        .param("email", otherUser.getEmail()))
                .andExpect(status().isFound());
    }

    @Then("{string} cannot see the friend request")
    public void userCannotSeeTheFriendRequest(String otherUserEmail) {
        otherUser = userService.getUserByEmail(otherUserEmail);
        currentUser = userService.getAuthenticatedUser();

        List<FriendRequest> otherUserFriendRequests = userService.getPendingFriendRequests(otherUser);
        Assertions.assertNotNull(otherUserFriendRequests);
        Assertions.assertFalse(otherUserFriendRequests.stream().anyMatch(request -> request.getSender().equals(currentUser)));
    }

    @Given("I see my friends list has {string}")
    public void i_see_my_friends_list_has(String friendEmail) {
        currentUser = userService.getAuthenticatedUser();
        System.out.println(currentUser);
        List<User> friends = currentUser.getFriends();
        System.out.println(friends);
        friendToCancel = userService.getUserByEmail(friendEmail);
        Assertions.assertNotNull(friends);
        Assertions.assertTrue(friends.stream().anyMatch(friend -> userService.areUsersEqual(friendToCancel, friend)));
    }

    @When("I click on a UI element that allows me to remove a {string} from my list")
    public void i_click_on_a_ui_element_that_allows_me_to_remove_a_from_my_list(String friendEmail) throws Exception{
        mvcResult = mockMvc.perform(get("/manage-friends"))
                .andExpect(status().isOk())
                .andReturn();
    }


    @When("I confirm that I want to remove {string}")
    public void i_confirm_that_i_want_to_remove(String friendToCancelEmail) throws Exception {
        mockMvc.perform(post("/manage-friends")
                .param("action", "remove")
                .param("email", friendToCancelEmail))
                .andExpect(status().isFound());
    }

    @Then("{string} is removed from my list of friends")
    public void is_removed_from_my_list_of_friends(String canceledFriendEmail) {
        currentUser = userService.getAuthenticatedUser();
        friendToCancel = userService.getUserByEmail(canceledFriendEmail);
        List<User> myFriends = currentUser.getFriends();
        Assertions.assertFalse(myFriends.stream().anyMatch(friend -> userService.areUsersEqual(friendToCancel, friend)));
    }

    @Then("I cannot see {string}'s gardens")
    public void i_cannot_see_s_gardens(String canceledFriendEmail) throws Exception {
        currentUser = userService.getAuthenticatedUser();
        friendToCancel = userService.getUserByEmail(canceledFriendEmail);
        Location canceledFriendLocation = new Location("123 Test Street", "Test Suburb", "Test City", "1234", "Test Country");
        Garden canceledFriendGarden = new Garden("Hello", canceledFriendLocation, "1", friendToCancel);
        gardenService.addGarden(canceledFriendGarden);
        Long canceledId = canceledFriendGarden.getId();
        mvcResult = mockMvc.perform(get("/view-gardens")
                .queryParam("id", canceledId.toString())).andExpect(status().is4xxClientError()).andReturn();
    }

    @Then("{string} cannot see my gardens")
    public void cannot_see_my_gardens(String canceledFriendEmail) throws Exception {
        currentUser = userService.getAuthenticatedUser();
        friendToCancel = userService.getUserByEmail(canceledFriendEmail);
        Location myLocation = new Location("123 My Street", "My Suburb", "Test City", "1234", "Test Country");
        Garden myGarden = new Garden("My Garden", myLocation, "1", currentUser);
        gardenService.addGarden(myGarden);
        Long myId = myGarden.getId();
        mvcResult = mockMvc.perform(get("/view-gardens")
                .queryParam("id", myId.toString())).andExpect(status().is4xxClientError()).andReturn();
    }

    @Then("I am removed from the list of friends of {string}")
    public void i_am_removed_from_the_list_of_friends_of(String canceledFriendEmail) {
        currentUser = userService.getAuthenticatedUser();
        friendToCancel = userService.getUserByEmail(canceledFriendEmail);
        List<User> theirFriends = friendToCancel.getFriends();
        Assertions.assertFalse(theirFriends.stream().anyMatch(friend -> userService.areUsersEqual(currentUser, friend)));
    }
}
