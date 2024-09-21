package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.Entity;

@Entity
public class ColourScheme extends Item {
    public ColourScheme() {
    }

    public ColourScheme(String name, Integer price, boolean isSellable) {
        super(name, price);
    }
}