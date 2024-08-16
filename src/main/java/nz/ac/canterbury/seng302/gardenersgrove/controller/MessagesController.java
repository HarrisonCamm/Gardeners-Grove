package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;

@Controller
public class MessagesController {

    Logger logger = LoggerFactory.getLogger(MessagesController.class);

    @GetMapping("/messages")
    public String getMessages() {
        logger.info("/GET messages");
        return "messagesTemplate";
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public Message sendMessage(Message message) {
        message.setTimestamp(new Date());
        return message;
    }


}
