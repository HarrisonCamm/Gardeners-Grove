package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("badge")
public class BadgeItem extends Item {
    @Column(length = 32)
    private String badge;

    protected BadgeItem() {
    }

    public BadgeItem(String name, boolean equipable, int price, String badge) {
        super(name, price);
        this.badge = badge;
    }

    public String getBadge() {
        return badge;
    }
}
