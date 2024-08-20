package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void saveMessage(String sender, String recipient, String content) {
        Message message = new Message(sender, recipient, content, new Date());
        messageRepository.save(message);
    }

    public List<Message> getConversation(String user1, String user2) {
        List<Message> messagesSent = messageRepository.findBySenderAndRecipientOrderByTimestampAsc(user1, user2);
        List<Message> messagesReceived = messageRepository.findByRecipientAndSenderOrderByTimestampAsc(user1, user2);

        // Combine the lists and sort by timestamp
        messagesSent.addAll(messagesReceived);
        messagesSent.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

        return messagesSent;
    }


}
