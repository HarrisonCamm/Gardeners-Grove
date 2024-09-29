package nz.ac.canterbury.seng302.gardenersgrove.unit.entity;

import nz.ac.canterbury.seng302.gardenersgrove.entity.BadgeItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Inventory;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void toString_ReturnsCorrectFormat() {
        User user = new User("John", "Doe", false, "john.doe@example.com", "Password1!", "01/01/1990");
        user.setUserId(1L);
        String expected = "User[id=1, firstName='John', lastName='Doe', email='john.doe@example.com', password='Password1!', dateOfBirth='01/01/1990']";
        assertEquals(expected, user.toString());
    }
}
