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

    @Autowired
    public MessagesController(UserService userService, SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @GetMapping("/messages")
    public String getMessages(Model model) {
        logger.info("/GET messages");

        String currentUserEmail = userService.getAuthenticatedUser().getEmail();
        model.addAttribute("from", currentUserEmail);
        model.addAttribute("friends", userService.getAuthenticatedUser().getFriends());

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

    @MessageMapping("/chat.send/{username}")
    public void sendMessage(@DestinationVariable String username, Message message) {
        logger.info("Sending message to {}: {}", username, message.getContent());
        messageService.saveMessage(message.getSender(), username, message.getContent());
        messagingTemplate.convertAndSendToUser(username, "/queue/reply", message);
    }

    @GetMapping("/chat/{username}")
    public @ResponseBody List<Message> getChat(@PathVariable String username) {
        String currentUser = userService.getAuthenticatedUser().getEmail();
        return messageService.getConversation(currentUser, username); // Return the list of messages as JSON
    }

}
