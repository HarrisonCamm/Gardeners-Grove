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
@Table(name = "USERS") //revise later, ask tutor about style
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //research how this works
    @Column(name = "user_id")
    private Long userId; //to make each user unique, maybe this could also just be the email but discuss later

    @Column(name = "firstName", length = 64) //first name must be 64 chars or less
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

    @Column
    private String filePath;

    @Lob
    @Column
    private byte[] image;

    @Column()
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<Authority> userRoles;

    public User() {
        // JPA empty constructor
    }

    public User(String email, String firstName, String lastName, String password) {
        this(firstName, lastName, false, email, password, "");
    }

    public User(String firstName, String lastName, boolean noLastName, String email, String password, String dateOfBirth) {
        this(null, firstName, lastName, noLastName, email, password, dateOfBirth);
    }

    public User(Long id, String firstName, String lastName, boolean noLastName, String email, String password, String dateOfBirth) {
        this.userId = id;
        this.password = password;
        this.setValues(firstName, lastName, noLastName, email, dateOfBirth);
    }

    public User setValues(String firstName, String lastName, boolean noLastName, String email, String dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.noLastName = noLastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public void grantAuthority(String authority) {
        if ( userRoles == null )
            userRoles = new ArrayList<>();
        userRoles.add(new Authority(authority));
    }

    public void grantAuthorities(List<String> roles) {
        if ( userRoles == null )
            userRoles = new ArrayList<>();
        for (String s: roles) {
            userRoles.add(new Authority(s));
        }
    }

    public List<GrantedAuthority> getAuthorities(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        this.userRoles.forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority.getRole())));
        return authorities;
    }

    public User(String firstName, String lastName, boolean noLastName, String email, String password, String dateOfBirth, String filePath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.noLastName = noLastName;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.filePath = filePath;
        this.image = null;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
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

    public void setFilePath(String newFilePath) {
        this.filePath = newFilePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setImage(byte[] newImage) {
        this.image = newImage;
    }

    public byte[] getImage() {
        return image;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, firstName='%s', lastName='%s', email='%s', password='%s', dateOfBirth='%s']",
                userId, firstName, lastName, email, password, dateOfBirth);
    }
}
