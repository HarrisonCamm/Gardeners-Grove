package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

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

    public void setIcon() {
        this.icon = icon;
    }
    public Image getIcon() {
        return this.icon;
    }
}