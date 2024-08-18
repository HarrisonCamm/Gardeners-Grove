package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;

@Controller
public class MessagesController {

    Logger logger = LoggerFactory.getLogger(MessagesController.class);

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MessagesController(UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/messages")
    public String getMessages(Model model) {
        logger.info("/GET messages");

        model.addAttribute("from", userService.getAuthenticatedUser().getEmail());

        return "messagesTemplateTest";
    }

    @MessageMapping("/chat.send/{username}")
    public void sendMessage(@DestinationVariable String username, Message message) {
        message.setTimestamp(new Date());
        messagingTemplate.convertAndSendToUser(username, "/queue/reply", message);
    }
}
