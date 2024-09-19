package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.Entity;

@Entity
public class ColourScheme extends AbstractItem {

    public ColourScheme(String name, Integer price, boolean isSellable) {
        super(name, price, isSellable);
    }
}