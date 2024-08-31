package nz.ac.canterbury.seng302.gardenersgrove.service;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Transaction;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private TransactionRepository transactionRepository;
    public Page<Transaction> findTransactionsByUserId(User user, Pageable pageable) {
        return transactionRepository.findAllByUser(user, pageable);
    }
}
