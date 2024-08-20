package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import rx.internal.util.unsafe.MessagePassingQueue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class MessagesController {

    Logger logger = LoggerFactory.getLogger(MessagesController.class);

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    /**
     * Constructor for the MessagesController
     * @param userService The user service
     * @param messagingTemplate The messaging template
     * @param messageService The message service
     */
    @Autowired
    public MessagesController(UserService userService, SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    /**
     * Get the messages page and add the required attributes to the model
     * @param model The model to add attributes to
     * @return The name of the template to render
     */
    @GetMapping("/messages")
    public String getMessages(Model model) {
        logger.info("/GET messages");

        // Add the current user's email to the model and the list of the user's friends
        String currentUserEmail = userService.getAuthenticatedUser().getEmail();
        model.addAttribute("from", currentUserEmail);
        model.addAttribute("friends", userService.getAuthenticatedUser().getFriends());

        // Add all the last messages sent
        List<String> lastMessages = new ArrayList<>();
        userService.getAuthenticatedUser().getFriends().forEach(friend -> {
            Optional<Message> lastMessage = messageService.getLastMessage(currentUserEmail, friend.getEmail());
            String lastMessageContent = lastMessage.map(Message::getContent).orElse("Start a conversation!");
            if (lastMessageContent.length() > 10) {
                lastMessageContent = lastMessageContent.substring(0, 10) + "...";
            }
            lastMessages.add(lastMessageContent);
        });
        model.addAttribute("lastMessages", lastMessages);

        return "messagesTemplateTest";
    }

    /**
     * Send a message to a user
     * @param username The username of the recipient
     * @param message The message to send
     */
    @MessageMapping("/chat.send/{username}")
    public void sendMessage(@DestinationVariable String username, Message message) {
        logger.info("Sending message to {}: {}", username, message.getContent());
        messageService.saveMessage(message.getSender(), username, message.getContent());
        messagingTemplate.convertAndSendToUser(username, "/queue/reply", message);
    }

    /**
     * Get the chat between the current user and another user
     * @param username The username of the other user
     * @return The list of messages between the two users sorted by the date they were sent
     */
    @GetMapping("/chat/{username}")
    public @ResponseBody List<Message> getChat(@PathVariable String username) {
        String currentUser = userService.getAuthenticatedUser().getEmail();
        return messageService.getConversation(currentUser, username); // Return the list of messages as JSON
    }

}
