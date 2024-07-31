package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column()
    private Long gardenID;

    @Column()
    private Long userID;

    @Column(nullable = false)
    private Boolean evaluated = false;

    @Column(nullable = false)
    private Boolean appropriate = false;

    protected Tag() {}

    // Constructor with only name
    public Tag(String name) {
        // Calls bigger constructor
        this(name, null, null, false, false);
    }

    // Constructor with all fields
    public Tag(String name, Long gardenID, Long userID, boolean evaluated, boolean appropriate) {
        this.name = name;
        this.gardenID = gardenID;
        this.userID = userID;
        this.evaluated = evaluated;
        this.appropriate = appropriate;
    }

    // tag id
    public Long getId() {return id; }
    // tag id
    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserID() {return userID; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getGardenId() {return gardenID; }

    // Sets whether the tag has been evaluated
    public void setEvaluated(boolean evaluated) { this.evaluated = evaluated; }

    // Sets whether the tag name is appropriate
    public void setAppropriate(boolean appropriate) { this.appropriate = appropriate; }
}