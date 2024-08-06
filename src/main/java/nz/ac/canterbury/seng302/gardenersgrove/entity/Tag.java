package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean evaluated = false;

    protected Tag() {}

    // Constructor
    public Tag(String name, boolean evaluated) {
        this.name = name;
        this.evaluated = evaluated;
    }

    // tag id
    public Long getId() {return id; }
    // tag id
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getEvaluated() {
        return evaluated;
    }

    // Sets whether the tag has been evaluated
    public void setEvaluated(boolean evaluated) { this.evaluated = evaluated; }
}