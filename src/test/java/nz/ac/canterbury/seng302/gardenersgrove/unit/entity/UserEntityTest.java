package nz.ac.canterbury.seng302.gardenersgrove.unit.entity;

import nz.ac.canterbury.seng302.gardenersgrove.entity.BadgeItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserEntityTest {

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
        BadgeItem badgeItem = new BadgeItem("Badge", 10, "\uD83C\uDF46", 3);
        user.addItem(badgeItem);
        assertFalse(user.getInventory().isEmpty());
        assertTrue(user.getInventory().contains(badgeItem));
    }

    @Test
    void addItem_AddsItemToInventoryWithQuantity() {
        User user = new User("John", "Doe", false, "", "", "");
        BadgeItem badgeItem = new BadgeItem("Badge", 10, "\uD83C\uDF46", 3);
        user.addItem(badgeItem);
        user.addItem(badgeItem);
        user.addItem(badgeItem);
        assertNotNull(user.getItem(badgeItem, 3));
        assertEquals(3, user.getItem(badgeItem, 3).getQuantity());
    }

    @Test
    void removeItem_RemovesItemFromInventory() {
        User user = new User("John", "Doe", false, "", "", "");
        BadgeItem badgeItem = new BadgeItem("Badge", 10, "\uD83C\uDF46", 3);
        user.addItem(badgeItem);
        user.removeItem(badgeItem, 1);
        assertTrue(user.getInventory().isEmpty());
    }

    @Test
    void removeItem_RemovesItemFromInventoryWithQuantity() {
        User user = new User("John", "Doe", false, "", "", "");
        BadgeItem badgeItem = new BadgeItem("Badge", 10, "\uD83C\uDF46", 3);
        user.addItem(badgeItem);
        user.addItem(badgeItem);
        user.addItem(badgeItem);
        user.removeItem(badgeItem, 2);
        assertNotNull(user.getItem(badgeItem, 1));
        assertEquals(1, user.getItem(badgeItem, 1).getQuantity());
    }

    @Test
    void removeItem_RemovesItemFromInventoryWithQuantityZero() {
        User user = new User("John", "Doe", false, "", "", "");
        BadgeItem badgeItem = new BadgeItem("Badge", 10, "\uD83C\uDF46", 3);
        user.addItem(badgeItem);
        user.removeItem(badgeItem, 0);
        assertEquals(1, user.getInventory().size());
        assertNotNull(user.getItem(badgeItem, 1));
        assertEquals(1, user.getItem(badgeItem, 1).getQuantity());
    }

    @Test
    void removeItem_RemovesItemFromInventoryWithQuantityGreaterThanInventory() {
        User user = new User("John", "Doe", false, "", "", "");
        BadgeItem badgeItem = new BadgeItem("Badge", 10, "\uD83C\uDF46", 3);
        user.addItem(badgeItem);
        user.addItem(badgeItem);
        user.addItem(badgeItem);
        user.addItem(badgeItem);
        assertThrows(IllegalArgumentException.class, () -> user.removeItem(badgeItem, 5));
        assertNotNull(user.getItem(badgeItem, 4));
        assertEquals(4, user.getItem(badgeItem, 4).getQuantity());
    }
}
