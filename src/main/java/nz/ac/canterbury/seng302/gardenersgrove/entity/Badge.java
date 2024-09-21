package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

// Badge class implementing Purchasable and Equipable
@Entity
public class Badge extends Item implements Equipable {

    @Column(nullable = false)
    private boolean isEquipped;

    public Badge() {
    }

    public Badge(String name, Integer price) {
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