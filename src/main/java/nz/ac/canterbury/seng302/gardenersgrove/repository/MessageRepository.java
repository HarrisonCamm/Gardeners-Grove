package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
    /**
     * Gets the messages sent by sender to recipient
     * @param sender The sender of the message
     * @param recipient The recipient of the message
     * @return A list of messages sent by sender to recipient
     */
    List<Message> findMessageBySenderAndRecipient(String sender, String recipient);
}
