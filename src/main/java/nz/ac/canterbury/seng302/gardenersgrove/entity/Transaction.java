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


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = true)
    private User sender; // Optional sender

    @ManyToOne(fetch = FetchType.EAGER)
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


    // This column will be used to determine if the transaction has been claimed by the receiver
    // In general, this will be true if you are playing plant guesser and daily spin
    // and false if you are tipping because the receiver has to claim the tip
    @Column
    private boolean claimed;

    //This column is only currently used for garden tips to determine which garden it is for
    //Explicitly set to optional but not sure if this is necessary
    @ManyToOne(optional = true)
    @JoinColumn(name = "tipped_garden", referencedColumnName = "id")
    private Garden tippedGarden;

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
        this.claimed = true;
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

    // Not sure if JPA requires naming of getters to be getClaimed I want to do isClaimed
    public boolean getClaimed() { return claimed; }

    public Garden getTippedGarden() { return tippedGarden; }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public void setPlant(Plant plant) {this.plant = plant; }

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

    public void setClaimed(boolean claimed) { this.claimed = claimed; }

    public void setTippedGarden(Garden tippedGarden) { this.tippedGarden = tippedGarden; }
}