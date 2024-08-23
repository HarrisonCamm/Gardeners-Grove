package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import java.util.Date;

//Boilerplate code for this was written by ChatGPT but was modified to fit our needs
@Entity
@Table(name = "TRANSACTIONS")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = true)
    private User sender; // Optional sender

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // Required receiver

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "transaction_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType; // Could be "tip", "purchase", "reward", etc.

    @ManyToOne
    @JoinColumn(name = "plant_id")
    private Plant plant; // Reference to the purchased plant



    @Column(name = "notes", length = 512)
    private String notes;

    public Transaction() {
        // JPA empty constructor
    }

    public Transaction(User sender, User receiver, Integer amount, Date transactionDate, String transactionType, Plant plant, String notes) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.plant = plant;
        this.notes = notes;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }


    public Integer getAmount() {
        return amount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getNotes() {
        return notes;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }


    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}