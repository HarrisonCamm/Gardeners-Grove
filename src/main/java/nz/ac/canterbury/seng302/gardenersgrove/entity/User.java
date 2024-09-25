package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Image image;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Item> inventory = new ArrayList<>();

    @Column()
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<Authority> userRoles;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();



    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer inappropriateTagCount = 0;

    @Column(name = "badgeURL")
    private String badgeURL;

    @Column(name = "imageURL")
    private String imageURL;


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
        this.badgeURL = null;
        this.imageURL = null;
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

    // Getter for inventory
    public List<Item> getInventory() {
        return inventory;
    }

    // Setter for inventory
    public void setInventory(List<Item> inventory) {
        this.inventory = inventory;
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


    //TODO: Modify/Change the logic here to work with the Badge/Item classes that were implemented on U6007-AC2
    public void setBadgeURL(String newBadgeURL) {
        this.badgeURL = newBadgeURL;
    }

    public String getBadgeURL() {
        return badgeURL;
    }





    //TODO: Modify this code so it works with the logic for Image/GIF items in U6007-AC2
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
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


    public void addItem(Item item) {
        Item theItem = getItem(item, 1);

        if (theItem != null) {
            theItem.setQuantity(theItem.getQuantity() + 1);
        } else {
            // If the item doesn't exist, set the owner and add it to the inventory
            item.setOwner(this);
            item.setQuantity(1);
            inventory.add(item);
        }
    }


    public void removeItem(Item item, int quantity) throws Exception {
        Item theItem = getItem(item, quantity);
        if (theItem == null) {
            throw new Exception("Insufficient quantity.");
        }

        theItem.setQuantity(theItem.getQuantity() - quantity);
        if (theItem.getQuantity() == 0) {
            inventory.remove(theItem);
        }
    }

    public Item getItem(Item item, int quantity) {
        return inventory.stream().filter(i -> i.equals(item) && i.getQuantity() >= quantity).findFirst().orElse(null);
    }

}
