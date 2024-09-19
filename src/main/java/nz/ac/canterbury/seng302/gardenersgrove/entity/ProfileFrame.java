package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.Entity;

@Entity
public class ProfileFrame extends AbstractItem implements Equipable {
    private boolean isEquipped;

    public ProfileFrame(String name, Integer price, boolean isSellable) {
        super(name, price, isSellable);
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