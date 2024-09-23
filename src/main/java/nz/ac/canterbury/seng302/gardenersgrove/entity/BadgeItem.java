package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

// Badge class implementing Purchasable and Equipable
@Entity
public class BadgeItem extends Item implements Equipable {

    @Column(nullable = false)
    private boolean isEquipped;

    @Column(nullable = false)
    private String emoji;

    public BadgeItem() {
    }

    public BadgeItem(String name, Integer price, String emoji, Integer quantity) {
        super(name, price, quantity);
        this.emoji = emoji;
        this.isEquipped = false;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    @Override
    public boolean isEquipped() {
        return isEquipped;
    }

    @Override
    public void setEquipped(boolean equipped) {
        this.isEquipped = equipped;
    }
}