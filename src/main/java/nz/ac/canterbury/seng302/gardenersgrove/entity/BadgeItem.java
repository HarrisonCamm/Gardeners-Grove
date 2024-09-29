package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

// Badge class extending Item
@Entity
@DiscriminatorValue("badge")
public class BadgeItem extends Item {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Image icon;

    @OneToMany(mappedBy = "appliedBadge")
    private List<User> usersWithBadge = new ArrayList<>();

    protected BadgeItem() {
    }

    public BadgeItem(String name, Integer price, Image icon, Integer quantity) {
        super(name, price, quantity);
        this.icon = icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    public Image getIcon() {
        return this.icon;
    }

    public List<User> getUsersWithBadge() {
        return usersWithBadge;
    }

    public void setUsersWithBadge(List<User> usersWithBadge) {
        this.usersWithBadge = usersWithBadge;
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