package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    public static final Integer DEFAULT_BALANCE = 500;

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

    @Column(name = "bloomBalance", nullable = false, columnDefinition = "integer default 500")
    private Integer bloomBalance = DEFAULT_BALANCE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "User_Friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Image image;

    @Column()
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<Authority> userRoles;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    public List<Transaction> getTransactions() {
        return transactions;
    }





    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer inappropriateTagCount = 0;

    public User() {
        // JPA empty constructor
    }

    public User(String email, String firstName, String lastName, String password) {
        this(firstName, lastName, false, email, password, "");
    }

    public User(String email, String firstName, String lastName, String password, String dateOfBirth) {
        this(firstName, lastName, false, email, password, dateOfBirth);
    }

    public User(String firstName, String lastName, boolean noLastName, String email, String password, String dateOfBirth) {
        this(null, firstName, lastName, noLastName, email, password, dateOfBirth);
    }

    public User(Long id, String firstName, String lastName, boolean noLastName, String email, String password, String dateOfBirth) {
        this.userId = id;
        this.password = password;
        this.bloomBalance = DEFAULT_BALANCE;
        this.setValues(firstName, lastName, noLastName, email, dateOfBirth);
    }

    public User(String firstName, String lastName, boolean noLastName, String email, String password, String dateOfBirth, Image image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.noLastName = noLastName;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.image = image;
        this.bloomBalance = DEFAULT_BALANCE;
    }

    public User setValues(String firstName, String lastName, boolean noLastName, String email, String dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.noLastName = noLastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.bloomBalance = DEFAULT_BALANCE;
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

    public Integer getBloomBalance() { return bloomBalance; }

    public void setBloomBalance(Integer bloomBalance) { this.bloomBalance = bloomBalance; }

    public String setPassword(String newPassword) {
        return this.password = newPassword;
    }
    public String getPassword() {
        return password;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public List<FriendRequest> getSentFriendRequests() {
        return null;
    }

    public int getInappropriateTagCount() {
        return inappropriateTagCount;
    }

    public void setInappropriateTagCount(int inappropriateTagCount) {
        this.inappropriateTagCount = inappropriateTagCount;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, firstName='%s', lastName='%s', email='%s', password='%s', dateOfBirth='%s']",
                userId, firstName, lastName, email, password, dateOfBirth);
    }

    /**
     * Adds a friend to the user's friend list
     * @param acceptedFriend the user to add as a friend
     */
    public void addFriend(User acceptedFriend) {
        friends.add(acceptedFriend);
    }

    /**
     * Removes a friend from the user's friend list.
     * @param friendToRemove The user to be removed from the friends list.
     */
    public void removeFriend(User friendToRemove) {

        friends.removeIf(friend -> friend.equals(friendToRemove));

    }

    public void removeAllFriends() {
        friends.clear();
    }

    public List<User> getFriends() {
        return friends;
    }


}





















