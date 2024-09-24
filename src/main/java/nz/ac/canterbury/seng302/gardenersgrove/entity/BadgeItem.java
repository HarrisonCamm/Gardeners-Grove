package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// Badge class implementing Purchasable and Equipable
@Entity
@DiscriminatorValue("badge")
public class BadgeItem extends Item {

    @Column(nullable = false)
    private String emoji;

    protected BadgeItem() {
    }

    public BadgeItem(String name, Integer price, String emoji, Integer quantity) {
        super(name, price, quantity);
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

}