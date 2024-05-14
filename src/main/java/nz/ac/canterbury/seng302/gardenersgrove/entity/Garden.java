package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

@Entity
public class Garden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Lob
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location", referencedColumnName = "id")
    private Location location;

    private String size;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User owner;

    /**
     * Required constructor
     */
    protected Garden() {}

    /**
     * Garden Constructor
     * @param name name
     * @param location location object
     * @param size size
     */
    public Garden(String name, Location location, String size) {
        this(name, location, size, null);
    }

    /**
     * Garden Constructor
     * @param name name
     * @param location location object
     * @param size size
     * @param owner owner
     */
    public Garden(String name, Location location, String size, User owner) {
        this.name = name;
        this.location = location;
        this.size = size;
        this.owner = owner;
    }

    // Getter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}