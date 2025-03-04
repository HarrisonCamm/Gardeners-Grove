package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

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

    @Column
    private Date lastFreeSpinUsed;

    @Column(name = "bloomBalance", nullable = false, columnDefinition = "integer default 500")
    private Integer bloomBalance = DEFAULT_BALANCE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "User_Friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "User_NonFriendContacts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "contact_id")
    )
    private List<User> nonFriendContacts = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Image image;

    @JoinColumn(name = "uploaded_image_id")
    private Long uploadedImageId;

    @Column()
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<Authority> userRoles;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Transaction> transactions = new ArrayList<>();

    @ManyToOne // BadgeItem ID
    @JoinColumn(name = "applied_badge_id")
    private BadgeItem appliedBadge;

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
    }

    public User setValues(String firstName, String lastName, boolean noLastName, String email, String dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.noLastName = noLastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.appliedBadge = null;
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

    public BadgeItem getAppliedBadge() {
        return appliedBadge;
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


    public void setAppliedBadge(BadgeItem badge) {
        this.appliedBadge = badge;
    }



    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public Long getUploadedImageId() {
        return uploadedImageId;
    }

    public void setUploadedImageId(Long previousImageId) {
        this.uploadedImageId = previousImageId;
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
        if (nonFriendContacts.contains(acceptedFriend)) {
            this.removeContact(acceptedFriend);
        }
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

    public Date getLastFreeSpinUsed() {
        return lastFreeSpinUsed;
    }

    public void updateLastFreeSpinUsed() {
        lastFreeSpinUsed = new Date();
    }

    public void resetLastFreeSpinUsed() {
        lastFreeSpinUsed = null;
    }

    /**
     * Adds a non-friend contact
     * @param contact the contact
     * @return true if added, false if already contained
     */
    public boolean addContact(User contact) {
        if (!nonFriendContacts.contains(contact)) {
            nonFriendContacts.add(contact);
            return true;
        }
        return false;
    }

    /**
     * Removes a non-friend contact
     * @param contact the contact
     */
    public void removeContact(User contact) {
        nonFriendContacts.removeIf(c -> c.equals(contact));
    }

    /**
     * Gets an immutable list of non-friend contacts
     * @return the list of contacts
     */
    public List<User> getNonFriendContacts() {
        return nonFriendContacts;
    }

    public List<User> getAllContacts() {
        List<User> contacts = new ArrayList<>(getFriends());
        contacts.addAll(getNonFriendContacts());
        return contacts;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    @Override
    public boolean equals(Object user) {
        if (!(user instanceof User)) {
            return false;
        }
        return this.email.equals(((User) user).email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, firstName, lastName);
    }
}
