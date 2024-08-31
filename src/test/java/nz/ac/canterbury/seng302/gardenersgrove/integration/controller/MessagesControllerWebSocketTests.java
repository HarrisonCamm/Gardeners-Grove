package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.MessagesController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessagesControllerWebSocketTests {

    @LocalServerPort
    private int port;

    @MockBean
    private UserService userService;

    @MockBean
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private StompSession stompSession;
    private BlockingQueue<Message> receivedMessages;

    @BeforeEach
    public void setUp() throws Exception {
        receivedMessages = new LinkedBlockingDeque<>();

        User loggedUser = new User("to@email.com", "to@email.com", "to@email.com", "password");
        when(userService.getAuthenticatedUser()).thenReturn(loggedUser);
        doNothing().when(messageService).saveMessage(any(Message.class));

        // Set up the WebSocket client and Stomp session
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompSession = stompClient.connect("ws://localhost:" + port + "/ws", new StompSessionHandlerAdapter() {}).get(5, TimeUnit.SECONDS);

        // Subscribe to /user/queue/reply immediately after connection
        stompSession.subscribe("/user/to@email.com/queue/reply", new SessionHandler());
    }

    @Test
    public void testSendMessage() throws Exception {
        String sendTo = "to@email.com";
        Message chatMessage = new Message();
        chatMessage.setSender("to@email.com");
        chatMessage.setRecipient(sendTo);
        chatMessage.setContent("Hello");
        chatMessage.setStatus("sent");

        assertThat(stompSession.isConnected()).isTrue();

        stompSession.send("/app/chat.send/" + sendTo, chatMessage);

        // Simulate the message being saved and sent to the recipient as it takes time
        Thread.sleep(50);

        verify(messageService, times(1)).saveMessage(any(Message.class));
        verify(messagingTemplate, times(1)).convertAndSendToUser(eq(sendTo), eq("/queue/reply"), any(Message.class));
    }

    @Test
    public void testReceiveMessage() throws Exception {
        String sendTo = "to@email.com";
        Message chatMessage = new Message();
        chatMessage.setSender("to@email.com");
        chatMessage.setRecipient(sendTo);
        chatMessage.setContent("Hello");
        chatMessage.setStatus("sent");

        assertThat(stompSession.isConnected()).isTrue();

        // Use a CountDownLatch to synchronize
        CountDownLatch latch = new CountDownLatch(1);

        // Modify SessionHandler to count down the latch
        SessionHandler sessionHandler = new SessionHandler() {
            @Override
            public void handleFrame(@NotNull StompHeaders headers, Object payload) {
                try {
                    receivedMessages.offer((Message) payload, 500, MILLISECONDS);
                    latch.countDown(); // Signal that the message has been received
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        stompSession.subscribe("/user/to@email.com/queue/reply", sessionHandler);
        stompSession.send("/app/chat.send/" + sendTo, chatMessage);

        // Wait for the latch to count down or timeout after 5 seconds
        boolean messageReceived = latch.await(5, TimeUnit.SECONDS);
        assertThat(messageReceived).isTrue(); // Ensure the message was received within the timeout

        Message response = receivedMessages.poll(); // Retrieve the received message from the queue
        assertNotNull(response); // Assert that the message is not null
    }

    private class SessionHandler extends StompSessionHandlerAdapter {
        @NotNull
        @Override
        public Type getPayloadType(@NotNull StompHeaders headers) {
            return Message.class;
        }

        @Override
        public void handleFrame(@NotNull StompHeaders headers, Object payload) {
            try {
                receivedMessages.offer((Message) payload, 500, MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
