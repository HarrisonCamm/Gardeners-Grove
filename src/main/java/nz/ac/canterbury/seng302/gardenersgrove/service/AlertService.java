package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Alert;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class AlertService {

    private final AlertRepository alertDismissalRepository;

    @Autowired
    public AlertService(AlertRepository alertDismissalRepository) {
        this.alertDismissalRepository = alertDismissalRepository;
    }

    /**
     * Checks if a specific alert for a specific user and garden is dismissed.
     * If the dismissal time has passed 24 hours, it resets the dismissal state.
     *
     * @param user The user who owns the garden.
     * @param garden The garden for which the alert is checked.
     * @param alertType The type of alert being checked (e.g., "hasNotRained", "isRaining").
     * @return True if the alert is currently dismissed, false otherwise.
     */
    public boolean isAlertDismissed(User user, Garden garden, String alertType) {
        // Check if the alert has been dismissed
        Optional<Alert> alertOpt = alertDismissalRepository.findByUserAndGardenAndAlertType(user, garden, alertType);

        // If alert record exists
        if (alertOpt.isPresent()) {
            Alert alert = alertOpt.get();
            Instant now = Instant.now();

            // Check if it has been 24 hours since the alert was dismissed
            if (now.isAfter(alert.getDismissedTime().plus(24, ChronoUnit.HOURS))) {
                // 24 hours have passed, update alert to not being dismissed
                alert.setDismissed(false);
                alertDismissalRepository.save(alert);
                return false;
            } else {
                // 24 hours have not passed return current alert dismissal state
                return alert.getDismissed();
            }
        }
        // The Alert has been dismissed as no record is found
        return false;
    }

    /**
     * Records that a specific alert for a specific user and garden has been dismissed.
     * Creates a new dismissal record if none exists, or updates the existing one.
     *
     * @param user The user who owns the garden.
     * @param garden The garden for which the alert is dismissed.
     * @param alertType The type of alert being dismissed (e.g., "hasNotRained", "isRaining").
     */
    public void dismissAlert(User user, Garden garden, String alertType) {
        Instant now = Instant.now();

        // Check if the alert has been dismissed
        Optional<Alert> alertOpt = alertDismissalRepository.findByUserAndGardenAndAlertType(user, garden, alertType);
        Alert alert;

        // If alert record exists
        if (alertOpt.isPresent()) {
            // Get alert
            alert = alertOpt.get();
        } else {
            // No record exists, create alert
            alert = new Alert();
            alert.setUser(user);
            alert.setGarden(garden);
            alert.setAlertType(alertType);
        }

        // Make alert state dismissed
        alert.setDismissed(true);
        // Set the alert dismissal time
        alert.setDismissedTime(now);
        // Add alert record
        alertDismissalRepository.save(alert);
    }
}
