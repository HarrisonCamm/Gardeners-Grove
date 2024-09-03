package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Save a message to the database
     * @param sender The sender of the message
     * @param recipient The recipient of the message
     * @param content The content of the message
     */
    public void saveMessage(String sender, String recipient, String content) {
        Message message = new Message(sender, recipient, content, new Date());
        messageRepository.save(message);
    }

    /**
     * Get all the messages between two users
     * @param currUserEmail The current user's email
     * @param fromUserEmail The email of the user to get messages from
     * @return A list of messages between the two users
     */
    public List<Message> getConversation(String currUserEmail, String fromUserEmail) {
        List<Message> messagesSent = messageRepository.findMessageBySenderAndRecipient(currUserEmail, fromUserEmail);
        List<Message> messagesReceived = messageRepository.findMessageBySenderAndRecipient(fromUserEmail, currUserEmail);

        // Combine the lists and sort by timestamp
        messagesSent.addAll(messagesReceived);
        messagesSent.sort(Comparator.comparing(Message::getTimestamp));

        return messagesSent;
    }

    /**
     * Get the last message between two users for displaying in the view
     * @param currUserEmail The current user's email
     * @param fromUserEmail The email of the user to get the last message from
     * @return The last message between the two users
     */
    public Optional<Message> getLastMessage(String currUserEmail, String fromUserEmail) {
        List<Message> messagesSent = messageRepository.findMessageBySenderAndRecipient(currUserEmail, fromUserEmail);
        List<Message> messagesReceived = messageRepository.findMessageBySenderAndRecipient(fromUserEmail, currUserEmail);

        // Combine the lists and sort by timestamp
        messagesSent.addAll(messagesReceived);
        messagesSent.sort(Comparator.comparing(Message::getTimestamp));

        return messagesSent.isEmpty() ? Optional.empty() : Optional.of(messagesSent.get(messagesSent.size() - 1));
    }


}
