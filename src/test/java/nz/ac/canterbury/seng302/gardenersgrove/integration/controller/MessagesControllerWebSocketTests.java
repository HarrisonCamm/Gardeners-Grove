package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private BlockingQueue<Message> blockingQueue;

    @BeforeEach
    public void setUp() throws Exception {
        User loggedUser = new User("test@email.com", "test", "user", "password");
        when(userService.getAuthenticatedUser()).thenReturn(loggedUser);

        blockingQueue = new LinkedBlockingQueue<>();
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompSession = stompClient.connect("ws://localhost:" + port + "/ws", new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
    }

    @Test
    @WithMockUser
    public void testSendMessage() throws Exception {
        String sendTo = "to@email.com";
        Message chatMessage = new Message();
        chatMessage.setSender("from@email.com");
        chatMessage.setRecipient(sendTo);
        chatMessage.setContent("Hello");
        chatMessage.setStatus("sent");

        stompSession.subscribe("/user/queue/reply", new StompFrameHandler() {
            @NotNull
            @Override
            public Type getPayloadType(@NotNull StompHeaders stompHeaders) {
                return Message.class;
            }

            @Override
            public void handleFrame(@NotNull StompHeaders stompHeaders, Object o) {
                if (o instanceof Message) {
                    try {
                        blockingQueue.put((Message) o);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Failed to put message in blocking queue", e);
                    }
                } else {
                    System.out.println("Received unexpected message type: " + o.getClass().getName());
                }
            }
        });

        stompSession.send("/app/chat.send/" + sendTo, chatMessage);

        Message receivedMessage = blockingQueue.poll(2, TimeUnit.SECONDS);

        assertNotNull(receivedMessage);

        verify(messageService, times(1)).saveMessage(any(Message.class));
        verify(messagingTemplate, times(1)).convertAndSendToUser(eq(sendTo), eq("/queue/reply"), any(Message.class));
    }
}