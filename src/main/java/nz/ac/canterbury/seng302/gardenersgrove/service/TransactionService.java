package nz.ac.canterbury.seng302.gardenersgrove.service;

import jakarta.persistence.EntityNotFoundException;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Transaction;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TransactionRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;


@Service
public class TransactionService {
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private PlantService Plant;
    private PlantRepository plantRepository;


    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              PlantRepository plantRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.plantRepository = plantRepository;
    }



    public Transaction addTransaction(int amount, String notes, Date transactionDate, String transactionType, Long receiverId, Long senderId, Long plantId) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setNotes(notes);
        transaction.setTransactionDate(transactionDate); // Use the provided transactionDate
        transaction.setTransactionType(transactionType);

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));
        transaction.setReceiver(receiver);

        if (senderId != null) {
            User sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
            transaction.setSender(sender);
        }

        if (plantId != null) {
            // Assuming you have a PlantRepository for handling plant entities.
            Plant plant = plantRepository.findById(plantId)
                    .orElseThrow(() -> new EntityNotFoundException("Plant not found"));
            transaction.setPlant(plant);
        }

        return transactionRepository.save(transaction);
    }


    public Page<Transaction> findTransactionsByUser(User currentUser, int page, int size) {
        return transactionRepository.findAllByUser(currentUser, PageRequest.of(page, size));
    }

}
