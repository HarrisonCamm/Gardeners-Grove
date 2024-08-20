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
import org.springframework.web.bind.annotation.ResponseBody;
import rx.internal.util.unsafe.MessagePassingQueue;

import java.util.Date;
import java.util.List;

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

        model.addAttribute("from", userService.getAuthenticatedUser().getEmail());
        model.addAttribute("friends", userService.getAuthenticatedUser().getFriends());

        return "messagesTemplateTest";
    }

    @MessageMapping("/chat.send/{username}")
    public void sendMessage(@DestinationVariable String username, Message message) {
        message.setTimestamp(new Date());
        messageService.saveMessage(message.getSender(), username, message.getContent());
        messagingTemplate.convertAndSendToUser(username, "/queue/reply", message);
    }

    @GetMapping("/chat/{username}")
    public @ResponseBody List<Message> getChat(@DestinationVariable String username) {
        String currentUser = userService.getAuthenticatedUser().getEmail();
        List<Message> conversation = messageService.getConversation(currentUser, username);
        return conversation; // Return the list of messages as JSON
    }

}
