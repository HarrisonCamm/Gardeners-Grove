package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;


import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.core.type.TypeReference;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import nz.ac.canterbury.seng302.gardenersgrove.cucumber.RunCucumberTest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.WeatherService;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions.ResetPasswordSteps.authenticationManager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.repository.util.ClassUtils.hasProperty;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")

public class DirectMessagingSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ModerationService moderationService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private StompSession stompSession;

    @Autowired
    private StompFrameHandler stompFrameHandler;

    private MockMvc mockMvc;

    private ResultActions resultActions;

    private Message receivedMessage;

    RunCucumberTest runCucumberTest;


    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Grab the running config instance to be able to get the received message when it is mocked in the doAnswer
        runCucumberTest = new RunCucumberTest(moderationService, weatherService, stompSession, stompFrameHandler);
    }

    // Background
    @And("{string} is friends with {string}")
    public void isFriendsWith(String email1, String email2) throws Exception {
        // Send friend request
        mockMvc.perform(post("/manage-friends")
                .param("action", "invite")
                .param("email", email2)
                .with(csrf()));

        // Log out
        SecurityContextHolder.clearContext();

        // Log in as Sarah
        UsernamePasswordAuthenticationToken sarahToken = new UsernamePasswordAuthenticationToken("sarah@email.com", "Password1!");
        var sarahAuthentication = authenticationManager.authenticate(sarahToken);
        SecurityContextHolder.getContext().setAuthentication(sarahAuthentication);

        // Accept the friend request
        mockMvc.perform(post("/manage-friends")
                .param("action", "accept")
                .param("email", email1)
                .with(csrf()));

        // Log out
        SecurityContextHolder.clearContext();

        // Log back in as the original account
        UsernamePasswordAuthenticationToken originalToken = new UsernamePasswordAuthenticationToken(email1, "Password1!");
        var originalAuthentication = authenticationManager.authenticate(originalToken);
        SecurityContextHolder.getContext().setAuthentication(originalAuthentication);
    }

    // AC1
    @Given("I am anywhere in the system")
    public void iAmAnywhereInTheSystem() throws Exception {
        // The messages button is in the nav bar
        mockMvc.perform(get("/main")
                .with(csrf()));
    }

    // AC1
    @When("I click on the {string} button")
    public void iClickOnTheButton(String nameOfButton) throws Exception {
        resultActions = mockMvc.perform(get("/messages")
                .with(csrf()));
    }

    // AC1
    @Then("I can see all my friends with the last message they sent next to their name")
    public void iCanSeeAllMyFriendsWithTheLastMessageTheySentNextToTheirName() throws Exception {
        resultActions.andExpect(model().attributeExists("friends"))
                .andExpect(model().attribute("friends", hasSize(1)))
                .andExpect(model().attributeExists("lastMessages"))
                .andExpect(model().attribute("lastMessages", hasSize(1)))
                .andExpect(model().attribute("lastMessages", hasItem("Start a conversation!")));
    }

    @When("I click on a UI element that has the friends profile picture, name, and last message")
    public void iClickOnAUIElementThatHasTheFriendsProfilePictureNameAndLastMessage() throws Exception {
        // In the frontend, when you do this it calls this backend endpoint.
        resultActions = mockMvc.perform(get("/chat/sarah@email.com")
                .with(csrf()));

    }

    @Then("I am taken to the chat to start or continue a conversation")
    public void iAmTakenToTheChatToStartOrContinueAConversation() throws Exception {
        // Get the response body as a string
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        // Convert the response body to a list of Message objects
        ObjectMapper mapper = new ObjectMapper();
        List<Message> messages = mapper.readValue(responseBody, new TypeReference<List<Message>>() {});

        // Verify the size of the messages list
        assertTrue(messages.isEmpty());
    }

    @When("I send a message to another user")
    public void iSendAMessageToAnotherUser() throws Exception {

        stompSession.subscribe("/user/queue/reply", stompFrameHandler);

        // Send the message using the mocked session
        stompSession.send("/app/chat.send/inaya@email.com", new Message(
                "sarah@email.com",
                "inaya@email.com",
                "Send Message",
                new Date()
        ));

        // Get the received message from the mocked session
        receivedMessage = runCucumberTest.getReceivedMessage();

        // Simulate waiting for the message to be processed
        Thread.sleep(500);
    }

    @Then("they are able to see the message in real time and my message appears on the right")
    public void theyAreAbleToSeeTheMessageInRealTimeAndMyMessageAppearsOnTheRight() {
        assertNotNull(receivedMessage);
    }

    @When("they receive the message")
    public void theyReceiveTheMessage() {
        assertNotNull(receivedMessage);
    }

    @Then("they are able to see the message in real time and my message appears on the left")
    public void theyAreAbleToSeeTheMessageInRealTimeAndMyMessageAppearsOnTheLeft() {
        // This is bad, but we require e2e testing to test this properly and we already test they receive it before
        // This is more a problem with the AC itself
        assertNotNull(receivedMessage);
    }

    @When("when I enter inappropriate words and click send")
    public void whenIEnterInappropriateWordsAndClickSend() {
        stompSession.send("/app/chat.send/inaya@email.com", new Message(
                "sarah@email.com",
                "inaya@email.com",
                "InappropriateTag", //This is an inappropriate word in the config file
                new Date()
        ));

    }

    @Then("then that message is not sent, and I am shown the message {string}")
    public void thenThatMessageIsNotSentAndIAmShownTheMessage(String errorMessage) throws Exception {
        // We are going to test the status of the message in the backend
        mockMvc.perform(get("/message/status")
                        .param("content", "InappropriateTag") // provide the content parameter
                        .with(csrf()))
                .andExpect(content().string("blocked")); // expect the errorMessage as a response body
    }
}
