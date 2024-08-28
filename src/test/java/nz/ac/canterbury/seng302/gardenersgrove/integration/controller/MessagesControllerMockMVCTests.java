package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@WebMvcTest
public class MessagesControllerMockMVCTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    AuthenticationManager authenticationManager;


    @MockBean
    private ImageService imageService;


    @MockBean
    private ModerationService moderationService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    private static User loggedUser;

    private static List<User> friends;

    private static ResultActions resultActions;

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private BlockingQueue<Message> blockingQueue;


    @BeforeEach
    public void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        // Create a user to be returned by the user service
        loggedUser = new User("test@email.com", "test", "user", "password");

        // Mock the user service to return the logged-in user
        when(userService.getAuthenticatedUser()).thenReturn(loggedUser);

        // Create a list of friends to be returned by the user service
        friends = List.of(new User("friend1@email.com", "friend1", "user", "password"),
                          new User("friend2@email.com", "friend2", "user", "password"));

        loggedUser.setFriends(friends);

        friends.forEach(friend -> {
            when(messageService.getLastMessage(loggedUser.getEmail(), friend.getEmail())).thenReturn(Optional.of(new Message(
                    loggedUser.getEmail(),
                    friend.getEmail(),
                    "content",
                    new Date()
            )));
        });

        blockingQueue = new LinkedBlockingQueue<>();
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompSession = stompClient.connect("ws://localhost:8080/ws", new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
    }

    @Test
    @WithMockUser
    public void testGetMessages() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/messages")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testModel() throws Exception {
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/messages")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("messagesTemplate"));

        resultActions.andExpect(model().attribute("from", loggedUser.getEmail()));
        resultActions.andExpect(model().attribute("friends", friends));
        resultActions.andExpect(model().attributeExists("lastMessages"));
    }
}
