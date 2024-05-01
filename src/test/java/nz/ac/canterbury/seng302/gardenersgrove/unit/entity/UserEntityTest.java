package nz.ac.canterbury.seng302.gardenersgrove.unit.entity;

import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserEntityTest {

    @Test
    public void toString_ReturnsCorrectFormat() {
        User user = new User("John", "Doe", false, "john.doe@example.com", "password123", "1990-01-01");
        user.setUserId(1L);
        String expected = "User[id=1, firstName='John', lastName='Doe', email='john.doe@example.com', password='password123', dateOfBirth='1990-01-01']";
        assertEquals(expected, user.toString());
    }

//    @Test
//    public void grantAuthority_AddsRoleToUserAuthorities() {
//        User user = new User("John", "Doe", false, "john.doe@example.com", "password123", "1990-01-01");
//        String authority = "ROLE_USER";
//        user.grantAuthority(authority);
//
//        assertTrue(user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals(authority)));
//    }
}
