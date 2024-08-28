package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class MessagesController {

    Logger logger = LoggerFactory.getLogger(MessagesController.class);

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final ModerationService moderationService;
    private static final int MAX_MESSAGE_LENGTH = 255;

    /**
     * Constructor for the MessagesController
     * @param userService The user service
     * @param messagingTemplate The messaging template
     * @param messageService The message service
     * @param moderationService The moderation service
     */
    public MessagesController(UserService userService,
                              SimpMessagingTemplate messagingTemplate,
                              MessageService messageService,
                              ModerationService moderationService) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.moderationService = moderationService;
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

        String defaultMessage = "Start a conversation!";

        // Add all the last messages sent
        List<String> lastMessages = new ArrayList<>();
        userService.getAuthenticatedUser().getFriends().forEach(friend -> {
            Optional<Message> lastMessage = messageService.getLastMessage(currentUserEmail, friend.getEmail());
            String lastMessageContent = lastMessage.map(Message::getContent).orElse(defaultMessage);
            if (lastMessageContent.length() > defaultMessage.length()) {
                lastMessageContent = lastMessageContent.substring(0, defaultMessage.length()) + "...";
            }
            lastMessages.add(lastMessageContent);
        });
        model.addAttribute("lastMessages", lastMessages);

        return "messagesTemplate";
    }

    /**
     * Send a message to a user
     * @param username The username of the recipient
     * @param message The message to send
     */
    @MessageMapping("/chat.send/{username}")
    public void sendMessage(@DestinationVariable String username, Message message) {

        if (message.getStatus().equals("sent")) {
            messageService.saveMessage(message);
            messagingTemplate.convertAndSendToUser(username, "/queue/reply", message);
        }
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

    @GetMapping("/message/status")
    public @ResponseBody String getMessageStatus(@RequestParam String content) {
        if (content.length() > MAX_MESSAGE_LENGTH) {
            return "blocked";
        }
        String term = moderationService.moderateText(content);
        return switch (term) {
            case "evaluation_error" -> "evaluating";
            case "null" -> "sent";
            default -> "blocked";
        };
    }

}
