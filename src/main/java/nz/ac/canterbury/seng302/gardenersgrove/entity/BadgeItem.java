package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

// Badge class extending Item
@Entity
@DiscriminatorValue("badge")
public class BadgeItem extends Item {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Image icon;

    protected BadgeItem() {
    }

    public BadgeItem(String name, Integer price, Image icon, Integer quantity) {
        super(name, price, quantity);
        this.icon = icon;
    }

    public Image getIcon() {
        return this.icon;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o))
            return false;
        if (!(o instanceof BadgeItem badge))
            return false;

        return getIcon().equals(badge.getIcon());
    }

    @Override
    public int hashCode() {
        return super.hashCode() + getIcon().hashCode();
    }
}