package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * User class that contains all the values a user should have
 */

@Entity
@Table(name = "TEMPORARY_USERS") // Adjust this as per your database schema
public class TemporaryUser {

    @Id
    @Column(name = "temporary_user_id")
    private Long temporaryUserId;

    @Column(name = "firstName", length = 64)
    private String firstName;

    @Column(name = "lastName", length = 64)
    private String lastName;

    @Column(name = "noLastName")
    private boolean noLastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "dateOfBirth")
    private String dateOfBirth;


    public TemporaryUser(Long temporaryUserId, String firstName, String lastName, boolean noLastName, String email, String password, String dateOfBirth) {
        this.temporaryUserId = temporaryUserId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.noLastName = noLastName;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
    }

    public TemporaryUser() {

    }

    public void setTemporaryUserId(Long temporaryUserId) {
        this.temporaryUserId = temporaryUserId;
    }

    public Long getTemporaryUserId() {
        return temporaryUserId;
    }

    public void setFirstName(String newName) {
        this.firstName = newName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String newName) {
        this.lastName = newName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setNoLastName(boolean noLastName) {
        this.noLastName = noLastName;
    }

    public boolean getNoLastName() {
        return noLastName;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setDateOfBirth(String newDateOfBirth) {
        this.dateOfBirth = newDateOfBirth;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String setPassword(String newPassword) {
        return this.password = newPassword;
    }
    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return String.format(
                "TemporaryUser[id=%d, firstName='%s', lastName='%s', email='%s', password='%s', dateOfBirth='%s']",
                temporaryUserId, firstName, lastName, email, password, dateOfBirth);
    }
}
