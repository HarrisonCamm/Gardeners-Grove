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
    private String count;          //Todo: change to Float

    @Column(length = 512)
    private String description;

    @Column
    @DateTimeFormat(pattern = "dd/MM/YYYY")
    private Date datePlanted;

    @Column
    private String filePath;

    @Lob
    @Column
    private byte[] image;

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
        this.garden = garden;
        this.name = name;
    }

    /**
     * Constructor for Plant class
     * @param garden Plant's garden
     * @param name Plant name
     * @param count Plant count
     * @param description Plant description
     * @param datePlanted Plant date planted
     */
    public Plant(Garden garden, String name, String count, String description, String datePlanted, String filePath) throws ParseException, IOException {
        this.garden = garden;
        this.name = name;
        this.count = count;
        this.description = description;
        this.datePlanted = new SimpleDateFormat("dd/MM/yyyy").parse(datePlanted);
        this.filePath = filePath;
        this.image = null;
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
    public void setDatePlanted(Date date) { this.datePlanted = date; }

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
    public Date getDatePlanted() { return datePlanted; }

    public String getPicture() {
        return filePath;
    }

    public void setPicture(String filePath) {
        this.filePath = filePath;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

}
