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

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void saveMessage(String sender, String recipient, String content) {
        Message message = new Message(sender, recipient, content, new Date());
        messageRepository.save(message);
    }

    public List<Message> getConversation(String currUserEmail, String fromUserEmail) {
        List<Message> messagesSent = messageRepository.findMessageBySenderAndRecipientOrderByTimestampAsc(currUserEmail, fromUserEmail);
        List<Message> messagesReceived = messageRepository.findByRecipientAndSenderOrderByTimestampAsc(currUserEmail, fromUserEmail);

        // Combine the lists and sort by timestamp
        messagesSent.addAll(messagesReceived);
        messagesSent.sort(Comparator.comparing(Message::getTimestamp));

        return messagesSent;
    }

    public Optional<Message> getLastMessage(String currUserEmail, String fromUserEmail) {
        List<Message> messagesSent = messageRepository.findMessageBySenderAndRecipientOrderByTimestampAsc(currUserEmail, fromUserEmail);
        List<Message> messagesReceived = messageRepository.findByRecipientAndSenderOrderByTimestampAsc(currUserEmail, fromUserEmail);

        // Combine the lists and sort by timestamp
        messagesSent.addAll(messagesReceived);
        messagesSent.sort(Comparator.comparing(Message::getTimestamp));

        return messagesSent.isEmpty() ? Optional.empty() : Optional.of(messagesSent.get(messagesSent.size() - 1));
    }


}
