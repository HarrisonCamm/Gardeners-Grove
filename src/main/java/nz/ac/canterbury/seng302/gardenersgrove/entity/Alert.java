package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "garden_id", nullable = false)
    private Garden garden;

    @Column(name = "alert_type", nullable = false)
    private String alertType;

    @Column(name = "dismissed", nullable = false)
    private Boolean dismissed;

    @Column(name = "dismissed_time", nullable = false)
    private Instant dismissedTime;

    public void setUser(User user) {
        this.user = user;
    }

    public void setGarden(Garden garden) {
        this.garden = garden;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public boolean getDismissed() {
        return this.dismissed;
    }

    public void setDismissed(boolean b) {
        this.dismissed = b;
    }

    public Instant getDismissedTime() {
        return this.dismissedTime;
    }

    public void setDismissedTime(Instant now) {
        this.dismissedTime = now;
    }
}
