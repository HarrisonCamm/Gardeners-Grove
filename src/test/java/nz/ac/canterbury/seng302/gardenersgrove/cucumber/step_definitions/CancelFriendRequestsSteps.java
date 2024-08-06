package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CancelFriendRequestsSteps {
    @And("{string} has not yet accepted the invite")
    public void userHasNotYetAcceptedTheInvite(String userEmail) {
        throw new io.cucumber.java.PendingException();
    }

    @When("I cancel my friend request")
    public void iCancelMyFriendRequest() {
        throw new io.cucumber.java.PendingException();
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
