package nz.ac.canterbury.seng302.gardenersgrove.unit.entity;

import nz.ac.canterbury.seng302.gardenersgrove.entity.BadgeItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import org.h2.util.Utils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void toString_ReturnsCorrectFormat() {
        User user = new User("John", "Doe", false, "john.doe@example.com", "Password1!", "01/01/1990");
        user.setUserId(1L);
        String expected = "User[id=1, firstName='John', lastName='Doe', email='john.doe@example.com', password='Password1!', dateOfBirth='01/01/1990']";
        assertEquals(expected, user.toString());
    }

    @Test
    void addItem_AddsItemToInventory() {

        User user = new User("John", "Doe", false, "", "", "");
        BadgeItem badgeItem = new BadgeItem("Badge", 10, new Image(), 3);
        user.addItem(badgeItem);
        assertFalse(user.getInventory().isEmpty());
        assertTrue(user.getInventory().contains(badgeItem));
    }

    @Test
    void addItem_AddsItemToInventoryWithQuantity() {
        User user = new User("John", "Doe", false, "", "", "");
        BadgeItem badgeItem = new BadgeItem("Badge", 10, new Image(), 3);
        user.addItem(badgeItem);
        user.addItem(badgeItem);
        user.addItem(badgeItem);
        assertNotNull(user.getItem(badgeItem, 3));
        assertEquals(3, user.getItem(badgeItem, 3).getQuantity());
    }

    @Test
    void removeItem_RemovesItemFromInventory() {
        User user = new User("John", "Doe", false, "", "", "");
        BadgeItem badgeItem = new BadgeItem("Badge", 10, new Image(), 3);
        user.addItem(badgeItem);
        user.removeItem(badgeItem, 1);
        assertTrue(user.getInventory().isEmpty());
    }

    @Test
    void removeItem_RemovesItemFromInventoryWithQuantity() {
        User user = new User("John", "Doe", false, "", "", "");
        BadgeItem badgeItem = new BadgeItem("Badge", 10, new Image(), 3);
        user.addItem(badgeItem);
        user.addItem(badgeItem);
        user.addItem(badgeItem);
        user.removeItem(badgeItem, 2);
        assertNotNull(user.getItem(badgeItem, 1));
        assertEquals(1, user.getItem(badgeItem, 1).getQuantity());
    }





    @Test
    void removeItem_RemovesItemFromInventoryWithQuantityZero() throws URISyntaxException, IOException {
        Path timtamImagePath = Paths.get(Objects.requireNonNull(getClass().getResource("/static/images/timtam.png")).toURI());
        byte[] timtamImageBytes = Files.readAllBytes(timtamImagePath);
        Image timtamImage = new Image(timtamImageBytes, "png", false);
        BadgeItem timtamBadge = new BadgeItem("Tim Tam", 100, timtamImage, 1);

        User user = new User("John", "Doe", false, "", "", "");
        user.addItem(timtamBadge);
        user.removeItem(timtamBadge, 0);
        assertEquals(1, user.getInventory().size());
        assertNotNull(user.getItem(timtamBadge, 1));
        assertEquals(1, user.getItem(timtamBadge, 1).getQuantity());
    }

    @Test
    void removeItem_RemovesItemFromInventoryWithQuantityGreaterThanInventory() throws IOException, URISyntaxException {
        Path vegemiteImagePath = Paths.get(Objects.requireNonNull(getClass().getResource("/static/images/vegemite.png")).toURI());
        byte[] vegemiteImageBytes = Files.readAllBytes(vegemiteImagePath);
        Image vegemiteImage = new Image(vegemiteImageBytes, "png", false);
        BadgeItem vegemiteBadge = new BadgeItem("Tim Tam", 100, vegemiteImage, 1);

        User user = new User("John", "Doe", false, "", "", "");
        user.addItem(vegemiteBadge);
        user.addItem(vegemiteBadge);
        user.addItem(vegemiteBadge);
        user.addItem(vegemiteBadge);
        assertThrows(IllegalArgumentException.class, () -> user.removeItem(vegemiteBadge, 5));
        assertNotNull(user.getItem(vegemiteBadge, 4));
        assertEquals(4, user.getItem(vegemiteBadge, 4).getQuantity());
    }
}
