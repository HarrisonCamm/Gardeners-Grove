package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

// Badge class implementing Purchasable and Equipable
@Entity
public class Badge extends Item implements Equipable {

    @Column(nullable = false)
    private boolean isEquipped;

    @Column(nullable = false)
    private String emoji;

    public Badge() {
    }

    public Badge(String name, Integer price, String emoji) {
        super(name, price);
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