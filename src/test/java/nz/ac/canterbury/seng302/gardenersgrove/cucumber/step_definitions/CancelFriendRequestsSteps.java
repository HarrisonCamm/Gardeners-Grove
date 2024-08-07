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
import nz.ac.canterbury.seng302.gardenersgrove.security.CustomAuthenticationProvider;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.List;

import static nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions.ResetPasswordSteps.authenticationManager;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    @Autowired
    CustomAuthenticationProvider authenticationManager;


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
        List<User> friends = currentUser.getFriends();
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
        friendToCancel = userService.getUserByEmail(canceledFriendEmail);

        //logging in as friendToCancel to create a garden to test that current user cannot see the created garden
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(canceledFriendEmail, friendToCancel.getPassword());
        var authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //creating the garden for friendToCancel
        ResultActions resultActions = mockMvc.perform(post("/create-garden")
                .param("name", "Kaia's Garden")
                .param("location.streetAddress", "")
                .param("location.suburb", "")
                .param("location.city", "Christchurch")
                .param("location.postcode", "")
                .param("location.country", "NZ")
                .param("size", "10")
                .with(csrf())); // Add CSRF token

        List<Garden> theirGardens = gardenService.getOwnedGardens(friendToCancel.getUserId());

        Long canceledId = theirGardens.get(0).getId();

        //logging back in as Liam
        UsernamePasswordAuthenticationToken myToken = new UsernamePasswordAuthenticationToken(currentUser.getEmail(), currentUser.getPassword());
        var myAuthentication = authenticationManager.authenticate(myToken);
        SecurityContextHolder.getContext().setAuthentication(myAuthentication);

        //checking that liam cannot see the cancelled friend's garden
        mvcResult = mockMvc.perform(get("/view-gardens")
                .queryParam("id", canceledId.toString())).andExpect(status().is4xxClientError()).andReturn();
    }

    @Then("{string} cannot see my gardens")
    public void cannot_see_my_gardens(String canceledFriendEmail) throws Exception {
        currentUser = userService.getAuthenticatedUser();
        friendToCancel = userService.getUserByEmail(canceledFriendEmail);
        ResultActions resultActions = mockMvc.perform(post("/create-garden")
                .param("name", "Kaia's Garden")
                .param("location.streetAddress", "")
                .param("location.suburb", "")
                .param("location.city", "Christchurch")
                .param("location.postcode", "")
                .param("location.country", "NZ")
                .param("size", "10")
                .with(csrf())); // Add CSRF token

        List<Garden> myGardens = gardenService.getOwnedGardens(currentUser.getUserId());

        Long myId = myGardens.get(0).getId();
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
