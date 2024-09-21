package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class ProfileFrame extends Item implements Equipable {

    @Column(nullable = false)
    private boolean isEquipped;

    public ProfileFrame() {
    }

    public ProfileFrame(String name, Integer price) {
        super(name, price);
        this.isEquipped = false;
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