package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "shop_items",
            joinColumns = @JoinColumn(name = "shop_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private Set<Item> availableItems = new HashSet<>();


    // Getters
    public Long getId() {
        return id;
    }

    public Set<Item> getAvailableItems() {
        return availableItems;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setAvailableItems(Set<Item> availableItems) {
        this.availableItems = availableItems;
    }


    public void addItem(Item item) {
        availableItems.add(item);
    }

    public void removeItem(Item item) {
        availableItems.remove(item);
    }

    public boolean hasItem(Item item) {
        return availableItems.contains(item);
    }

}
