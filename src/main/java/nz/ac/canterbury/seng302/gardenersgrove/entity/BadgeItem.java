package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// Badge class extending Item
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

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o))
            return false;
        if (!(o instanceof BadgeItem badge))
            return false;

        return getEmoji().equals(badge.getEmoji());
    }
}