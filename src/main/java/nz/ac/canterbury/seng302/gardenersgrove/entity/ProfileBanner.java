package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.Entity;

@Entity
public class ProfileBanner extends AbstractItem implements Equipable {
    private boolean isEquipped;

    public ProfileBanner(String name, Integer price, boolean isSellable) {
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
