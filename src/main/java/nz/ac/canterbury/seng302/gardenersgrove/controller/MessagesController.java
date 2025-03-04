package nz.ac.canterbury.seng302.gardenersgrove.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class MessagesController {

    Logger logger = LoggerFactory.getLogger(MessagesController.class);

    private final UserService userService;
    private final GardenService gardenService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final ModerationService moderationService;
    private static final int MAX_MESSAGE_LENGTH = 255;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructor for the MessagesController
     * @param userService The user service
     * @param gardenService The garden service
     * @param messagingTemplate The messaging template
     * @param messageService The message service
     * @param moderationService The moderation service
     */
    public MessagesController(UserService userService,
                              SimpMessagingTemplate messagingTemplate,
                              MessageService messageService,
                              ModerationService moderationService,
                              GardenService gardenService) {
        this.userService = userService;
        this.gardenService = gardenService;
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
    public String getMessages(Model model,
                              @RequestParam(value = "gardenID", required = false) Long gardenID) {
        logger.info("GET /messages");

        Garden garden = null;
        if (gardenID != null) {
            Optional<Garden> foundGarden = gardenService.findGarden(gardenID);
            if (foundGarden.isEmpty() || !foundGarden.get().getIsPublic()) {
                return "redirect:/messages";
            }
            garden = foundGarden.get();
            model.addAttribute("gardenID", gardenID);
            User owner = garden.getOwner();
            model.addAttribute("ownerID", owner.getUserId());
            model.addAttribute("firstName", owner.getFirstName());
            model.addAttribute("lastName", owner.getLastName());
            model.addAttribute("email", owner.getEmail());
        }

        // Add the current user's email to the model and the list of the user's friends
        String currentUserEmail = userService.getAuthenticatedUser().getEmail();
        List<User> friends = userService.getAuthenticatedUser().getFriends();
        List<User> contacts = userService.getAuthenticatedUser().getAllContacts();
        if (garden != null && !contacts.contains(garden.getOwner())) {
            contacts.add(garden.getOwner());
        }
        model.addAttribute("from", currentUserEmail);
        model.addAttribute("friends", friends);
        model.addAttribute("contacts", contacts);
        try {
            List<String> contactEmails = contacts.stream().map(User::getEmail).toList();
            model.addAttribute("contactEmails", objectMapper.writeValueAsString(contactEmails));
        } catch (Exception e) {
            logger.error("unable to convert contacts list to JSON");
        }


        String defaultMessage = "Start a conversation!";

        // Add all the last messages sent
        List<String> lastMessages = new ArrayList<>();
        contacts.forEach(contact -> {
            Optional<Message> lastMessage = messageService.getLastMessage(currentUserEmail, contact.getEmail());
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
     * @param recipientEmail The username of the recipient
     * @param message The message to send
     */
    @MessageMapping("/chat.send/{recipientEmail}")
    public void sendMessage(@DestinationVariable String recipientEmail, Message message) {
        if (message.getStatus().equals("sent")) {
            messageService.saveMessage(message);
            messagingTemplate.convertAndSendToUser(recipientEmail, "/queue/reply", message);

            User sender = userService.getUserByEmail(message.getSender());
            User recipient = userService.getUserByEmail(recipientEmail);
            if (!sender.getFriends().contains(recipient) && sender.addContact(recipient)) {
                userService.addUser(sender);
            }
            if (!recipient.getFriends().contains(sender) && recipient.addContact(sender)) {
                userService.addUser(recipient);
            }
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

    @GetMapping("/contacts")
    public @ResponseBody List<String> getContacts(@RequestParam(value = "email", required = false) String email) {
        logger.info("GET /contacts");

        List<String> contacts = userService.getAuthenticatedUser().getAllContacts().stream().map(User::getEmail).toList();
        if (email != null && !contacts.contains(email)) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException ie) {
                logger.error("interrupted while waiting for contact to be added");
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logger.error("exception occurred while waiting for contact to be added");
            }
        }
        return contacts;
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
