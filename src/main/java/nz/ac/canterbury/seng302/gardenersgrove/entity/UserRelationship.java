package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

@Entity
public class UserRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver")
    private User receiver;

    @Column(name = "status")
    private String status;

    public UserRelationship() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public User getReceiver() {
        return receiver;
    }
    public User getSender() {
        return sender;
    }

    public UserRelationship(User from, User to, String status) {
        this.sender = from;
        this.receiver = to;
        this.status = status;
    }
}
