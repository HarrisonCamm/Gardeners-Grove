package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.FriendRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")

public class CancelFriendRequestsSteps {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    UserService userService;

    private MockMvc mockMvc;
    private User otherUser;
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
    public void userCannotSeeTheFriendRequest(String userEmail) {
        throw new io.cucumber.java.PendingException();
    }

    @And("{string} cannot accept the friend request")
    public void userCannotAcceptTheFriendRequest(String userEmail) {
        throw new io.cucumber.java.PendingException();
    }
}
