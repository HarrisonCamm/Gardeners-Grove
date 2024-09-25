package nz.ac.canterbury.seng302.gardenersgrove.service;

import jakarta.persistence.EntityNotFoundException;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Transaction;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TransactionRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final PlantRepository plantRepository;


    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              PlantRepository plantRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.plantRepository = plantRepository;
    }

    public Transaction addTransaction(int amount, String notes, String transactionType, Long receiverId, Long senderId) {
        return addTransaction(amount, notes, transactionType, receiverId, senderId, null, null);
    }

    public Transaction addTransaction(int amount, String notes, String transactionType, Long receiverId, Long senderId, Garden tippedGarden) {
        return addTransaction(amount, notes, transactionType, receiverId, senderId, null, tippedGarden);
    }


    public Transaction addTransaction(int amount, String notes, String transactionType, Long receiverId, Long senderId, Long plantId, Garden tippedGarden) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setNotes(notes);
        transaction.setTransactionDate(new Date()); // Fill with current date of transaction
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
            Plant plant = plantRepository.findById(plantId)
                    .orElseThrow(() -> new EntityNotFoundException("Plant not found"));
            transaction.setPlant(plant);
        }

        if (tippedGarden != null) {
            transaction.setTippedGarden(tippedGarden);
        }

        return transactionRepository.save(transaction);
    }

    /**
     * Retrieves a Users transactions history sorted by newest first
     * @param currentUser User to retrieve transactions from
     * @param page  Selected page for pagination
     * @param size  How many transactions per page
     * @return All user transactions sorted by transactionDate the newest first
     */
    public Page<Transaction> findTransactionsByUser(User currentUser, int page, int size) {
        //Sorted after retrieved from repository before pagination
        return transactionRepository.findAllByUser(currentUser, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionDate")));
    }

    /**
     * Sets the claimed status of a transaction this is used after a tip transaction is created
     * @param transactionId The transaction to set claimed status
     * @param b The status to set the transaction to
     */
    public void setClaimed(Long transactionId, boolean b) {
        Transaction transaction = transactionRepository.findById(transactionId).get();
        transaction.setClaimed(b);
        String gardenName = transaction.getTippedGarden().getName();
        transaction.setNotes("Tipped " + gardenName + " (claimed)");
        transactionRepository.save(transaction);
    }

    public void setTippedGarden(Long transactionId, Garden garden) {
        Transaction transaction = transactionRepository.findById(transactionId).get();
        transaction.setTippedGarden(garden);
        transactionRepository.save(transaction);
    }

    public List<Transaction> retrieveGardenTips(User receiver, Garden tippedGarden) {
        return transactionRepository.findAllByReceiverAndTippedGardenAndClaimedFalse(receiver, tippedGarden);
    }

    public int totalUnclaimedTips(User receiver, Garden tippedGarden) {
        List<Transaction> transactions = retrieveGardenTips(receiver, tippedGarden);
        int total = 0;
        for (Transaction transaction : transactions) {
            total += transaction.getAmount();
        }
        return total;
    }

    public void claimAllGardenTips(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            setClaimed(transaction.getTransactionId(), true);
        }
    }
}
