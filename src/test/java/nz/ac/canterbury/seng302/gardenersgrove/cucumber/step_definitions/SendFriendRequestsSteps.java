package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.FriendRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.entity.UserRelationship;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendRequestService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserRelationshipService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SendFriendRequestsSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;
    @Autowired
    UserRelationshipService userRelationshipService;

    @Autowired
    FriendRequestService friendRequestService;
    private MockMvc mockMvc;
    private MvcResult mvcResult;
    private String searchQuery;
    private User friend;
    private User currentUser;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @Given("I am logged in")
    public void i_am_logged_in() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("liam@email.com", "Password1!");
        var authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        currentUser = userService.getAuthenicatedUser();
        currentUser.removeAllFriends();
        userService.updateUserFriends(currentUser);
        userRelationshipService.removeAll();
    }

//    AC 1
    @Given("I am anywhere on the app")
    public void i_am_anywhere_on_the_app() throws Exception{
         mockMvc.perform(get("/main"))
                 .andExpect(status().isOk());
    }

//    AC 1
    @When("I click on a UI element that allows me to send friend requests")
    public void i_click_on_a_ui_element_that_allows_me_to_send_friend_requests() throws Exception{
        mvcResult = mockMvc.perform(get("/manage-friends"))
                .andExpect(status().isOk())
                .andReturn();
    }

//    AC 1
    @Then("I am shown a manage friends page")
    public void i_am_shown_a_manage_friends_page() {
        String viewName = Objects.requireNonNull(mvcResult.getModelAndView()).getViewName();
        Assertions.assertEquals("manageFriendsTemplate", viewName);
    }

//    AC 2, 3
    @Given("I am on the manage friends page")
    public void i_am_on_the_manage_friends_page() throws Exception{
        mvcResult = mockMvc.perform(get("/manage-friends"))
                .andExpect(view().name("manageFriendsTemplate"))
                .andExpect(status().isOk())
                .andReturn();
    }

//    AC 2
    @Then("I see the list of my friends with their names and their profile pictures")
    public void i_see_the_list_of_my_friends_with_their_names_and_their_profile_pictures() {
        ModelAndView modelAndView = mvcResult.getModelAndView();
        List<User> friends = (List<User>) modelAndView.getModel().get("friends");
        for (User friend : friends) {
            Assertions.assertNotNull(friend.getFirstName());
            if (!friend.getNoLastName()) {
                Assertions.assertNotNull(friend.getLastName());
            }
            Assertions.assertNotNull(friend.getImage());
        }
    }

//    AC 2
    @And("a link to their gardens list including private and public gardens")
    public void a_link_to_their_gardens_list_including_private_and_public_gardens() {
        //Todo update this when the functionality is there
    }

//    AC 3
    @When("I hit the add friend button")
    public void i_hit_the_add_friend_button() {
        ModelAndView modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        modelAndView.addObject("showSearch", true);
    }

//    AC 3
    @Then("I see a search bar")
    public void i_see_a_search_bar() {
        ModelAndView modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        Assertions.assertTrue((Boolean) modelAndView.getModel().get("showSearch"));

    }

//    AC 4
    @And("I have opened the search bar")
    public void i_have_opened_the_search_bar() {
        ModelAndView modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        modelAndView.addObject("showSearch", true);
    }

//    AC 4
    @When("I enter a full name {string}")
    public void i_enter_a_full_name(String name) {
        searchQuery = name;
    }

//    AC 4, 5, 6
    @And("I hit the search button")
    public void i_hit_the_search_button() throws Exception{
        mvcResult = mockMvc.perform(post("/manage-friends")
                        .param("action", "search")
                        .param("searchQuery", searchQuery))
                .andExpect(status().isOk())
                .andReturn();
    }

//    AC 4
    @Then("I can see a list of users of the app exactly matching the name I provided")
    public void i_can_see_a_list_of_users_of_the_app_exactly_matching_the_name_i_provided() {
        ModelAndView modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        List<User> users = (List<User>) modelAndView.getModel().get("matchedUsers");
        for (User user: users) {
            String fullName;
            if (user.getNoLastName()) {
                fullName = user.getFirstName();
            } else {
                fullName = user.getFirstName() + " " + user.getLastName();
            }
            Assertions.assertEquals(searchQuery, fullName);
        }
    }

//    AC 5
    @When("I enter an email address {string}")
    public void i_enter_an_email_address(String email) {
        searchQuery = email;
    }

//    AC 5
    @Then("I can see a list of users of the app exactly matching the email provided")
    public void i_can_see_a_list_of_users_of_the_app_exactly_matching_the_email_provided() {
        ModelAndView modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        List<User> users = (List<User>) modelAndView.getModel().get("matchedUsers");
        for (User user: users) {
            Assertions.assertEquals(searchQuery, (user.getEmail()));
        }
    }

//    AC 6
    @When("I enter a search string {string}")
    public void i_enter_a_search_string(String searchString) {
        searchQuery = searchString;
    }

//    AC 6
    @And("there are no perfect matches")
    public void there_are_no_perfect_matches() {
        ModelAndView modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        List<User> users = (List<User>) modelAndView.getModel().get("matchedUsers");
        Assertions.assertNull(users);
    }

//    AC 6
    @Then("I see a message saying {string}")
    public void i_see_a_message_saying(String message) {
        ModelAndView modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        Assertions.assertEquals(message, modelAndView.getModel().get("searchResultMessage"));
    }

//    AC 7
    @And("I see a matching person for the search I made")
    public void i_see_a_matching_person_for_the_search_i_made() {
        ModelAndView modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        List<User> friends = (List<User>) modelAndView.getModel().get("matchedUsers");
        friend = friends.get(0);
        String fullName;
        if (friend.getNoLastName()) {
            fullName = friend.getFirstName();
        } else {
            fullName = friend.getFirstName() + " " + friend.getLastName();
        }
        Assertions.assertTrue(searchQuery.equals(fullName) || searchQuery.equals(friend.getEmail()));
    }

//    AC 7
    @When("I hit the invite as friend button")
    public void i_hit_the_invite_as_friend_button() throws Exception {
        mockMvc.perform(post("/manage-friends")
                        .param("action", "invite")
                        .param("email", friend.getEmail()))
                .andExpect(status().isFound())
                .andReturn();
    }

    @Then("the other user receives an invite that will be shown in their manage friends page")
    public void the_other_user_receives_an_invite_that_will_be_shown_in_their_manage_friends_page() {
        List<FriendRequest> friendRequests = userService.getPendingFriendRequests(friend);
        boolean receivesInvite = friendRequests.stream()
                .anyMatch(request -> userService.areUsersEqual(request.getSender(), currentUser));
        Assertions.assertTrue(receivesInvite);
    }

//    AC 8
    @And("I have pending invites from {string}")
    public void i_have_pending_invites_from(String email) {
        friend = userService.getUserByEmail(email);
        currentUser = userService.getAuthenicatedUser();
        FriendRequest friendRequest = new FriendRequest(friend, currentUser);
        friendRequestService.save(friendRequest);

        List<FriendRequest> friendRequests = userService.getPendingFriendRequests(currentUser);
        Assertions.assertNotNull(friendRequests);
        Assertions.assertTrue(friendRequests.stream().anyMatch(request -> request.getStatus().equals("Pending")));
    }

//    AC 8
    @When("I accept an invite")
    public void i_accept_an_invite() throws Exception {
        mockMvc.perform(post("/manage-friends")
                        .param("action", "accept")
                        .param("email", friend.getEmail()))
                .andExpect(status().isFound());
    }

//    AC 8
    @Then("that person is added to my list of friends")
    public void that_person_is_added_to_my_list_of_friends() {
        currentUser = userService.getAuthenicatedUser();
        List<User> friends = currentUser.getFriends();
        Assertions.assertTrue(friends.stream().anyMatch(friendCheck -> userService.areUsersEqual(friendCheck, friend)));
    }

//    AC 8
    @And("I can see their profile")
    public void i_can_see_their_profile() throws Exception {
        mvcResult = mockMvc.perform(get("/manage-friends"))
                .andExpect(status().isOk())
                .andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        List<User> friends = (List<User>) modelAndView.getModel().get("friends");
        Assertions.assertTrue(friends.stream().anyMatch(friendCheck -> userService.areUsersEqual(friendCheck, friend)));
    }

//    AC 8
    @And("I am added to that person’s friends list")
    public void i_am_added_to_that_persons_friends_list() {
        currentUser = userService.getAuthenicatedUser();
        friend = userService.getUserByEmail(friend.getEmail());
        List<User> friends = friend.getFriends();
        Assertions.assertTrue(friends.stream().anyMatch(friendCheck -> userService.areUsersEqual(friendCheck, currentUser)));
    }

    @And("they log into their account with {string} and {string}")
    public void they_log_into_their_account_with_user_and_password(String username, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        var authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

//    Ac 8
    @And("that person can see my profile")
    public void that_person_can_see_my_profile() throws Exception {
        mvcResult = mockMvc.perform(get("/manage-friends"))
                .andExpect(status().isOk())
                .andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        List<User> friends = (List<User>) modelAndView.getModel().get("friends");
        Assertions.assertTrue(friends.stream().anyMatch(friendCheck -> userService.areUsersEqual(friendCheck, currentUser)));
    }

//    AC 9
    @When("I decline an invite")
    public void i_decline_an_invite() throws Exception {
        mockMvc.perform(post("/manage-friends")
                        .param("action", "delete")
                        .param("email", friend.getEmail()))
                .andExpect(status().isFound());
    }

//    AC 9
    @Then("that person is not added to my list of friends")
    public void that_person_is_not_added_to_my_list_of_friends() {
        currentUser = userService.getAuthenicatedUser();
        List<User> friends = currentUser.getFriends();
        Assertions.assertFalse(friends.stream().anyMatch(friendCheck -> userService.areUsersEqual(friendCheck, friend)));
    }

//    AC 9
    @And("they cannot invite me anymore")
    public void they_cannot_invite_me_anymore() throws Exception {
        mvcResult = mockMvc.perform(post("/manage-friends")
                        .param("action", "search")
                        .param("searchQuery", currentUser.getEmail()))
                .andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        List<User> users = (List<User>) modelAndView.getModel().get("matchedUsers");
        Assertions.assertNull(users);
        UserRelationship relationship = userRelationshipService.getRelationship(currentUser, friend);
        Assertions.assertEquals("Declined", relationship.getStatus());
    }

//    AC 10
    @Given("I have sent an invite to {string}")
    public void i_have_sent_an_invite(String userEmail) throws Exception {
        mockMvc.perform(post("/manage-friends")
                        .param("action", "invite")
                        .param("email", userEmail))
                .andExpect(status().isFound())
                .andReturn();

    }

//    AC 10
    @And("they leave or decline the invite {string}")
    public void they_leave_or_decline_the_invite(String status) throws Exception {
        if (status.equals("Declined")) {
            mockMvc.perform(post("/manage-friends")
                            .param("action", "delete")
                            .param("email", currentUser.getEmail()))
                    .andExpect(status().isFound());
        }
    }

//    AC 10
    @When("I log in and check the status of the invite")
    public void i_log_in_and_check_the_status_of_the_invite() throws Exception {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("liam@email.com", "Password1!");
        var authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        mvcResult = mockMvc.perform(get("/manage-friends"))
                .andExpect(status().isOk())
                .andReturn();
    }

//    AC 10
    @Then("I can see the status of the invite as one of {string}, or {string}")
    public void i_can_see_the_status_of_the_invite_as_one_of_or(String pending, String declined) {
        ModelAndView modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        List<FriendRequest> friendRequests = (List<FriendRequest>) modelAndView.getModel().get("sentRequests");
        System.out.println(friendRequests);
        Assertions.assertTrue(friendRequests
                .stream().anyMatch(request ->
                        (request.getStatus().equals(pending) || request.getStatus().equals(declined))));
    }

}
