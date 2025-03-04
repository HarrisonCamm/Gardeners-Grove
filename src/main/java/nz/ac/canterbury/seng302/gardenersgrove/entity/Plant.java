package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long Id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Garden garden;

    @Column(nullable = false)
    private String name;

    @Column
    private String count;

    @Column(length = 512)
    private String description;

    @Column
    private String datePlanted;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Image image;

    /**
     * Required constructor
     */
    protected Plant() {}

    /**
     * Constructor for Plant class
     * @param garden Plant's garden
     * @param name Plant name
     */
    public Plant(Garden garden, String name) {
        this(garden, name, null, null, null);
    }

    /**
     * Constructor for Plant class
     * @param garden Plant's garden
     * @param name Plant name
     * @param count Plant count
     * @param description Plant description
     * @param datePlanted Plant date planted
     */
    public Plant(Garden garden, String name, String count, String description, String datePlanted) {
        this(garden, name, count, description, datePlanted, null);
    }

    /**
     * Constructor for Plant class
     * @param garden Plant's garden
     * @param name Plant name
     * @param count Plant count
     * @param description Plant description
     * @param datePlanted Plant date planted
     * @param image Plant image
     */
    public Plant(Garden garden, String name, String count, String description, String datePlanted, Image image) {
        this.garden = garden;
        this.name = name;
        this.count = count;
        this.description = description;
        this.datePlanted = datePlanted;
        this.image = image;
    }

    // Setter for id
    public void setId(Long id) { this.Id = id; }

    // Setter for name
    public void setName(String name) { this.name = name; }

    //Setter for count
    public void setCount(String count) { this.count = count; }

    //Setter for description
    public void setDescription(String description) { this.description = description; }

    //Setter for date
    public void setDatePlanted(String date) { this.datePlanted = date; }

    //Setter for garden
    public void setGarden(Garden garden) {
        this.garden = garden;
    }

    //Getter for Id
    public Long getId() {
        return Id;
    }


    //Getter for garden
    public String getName() { return name; }

    //Getter for name
    public Garden getGarden() { return garden; }

    //Getter for count
    public String getCount() { return count; }

    //Getter for description
    public String getDescription() { return description; }

    //Getter for datePlanted
    public String getDatePlanted() { return datePlanted; }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
