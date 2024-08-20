package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findMessageBySenderAndRecipientOrderByTimestampAsc(String sender, String recipient);
    List<Message> findByRecipientAndSenderOrderByTimestampAsc(String recipient, String sender);
}
