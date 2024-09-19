package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "inventory", cascade = CascadeType.ALL)
    private Inventory inventory;

    // Getter for id
    public Long getId() {
        return id;
    }

    // Setter for id
    public void setId(Long id) {
        this.id = id;
    }

    // Delegate the getItems() call to the inventory
    public List<AbstractItem> getItems() {
        if (inventory != null) {
            return inventory.getItems();
        } else {
            return new ArrayList<>(); // Return an empty list if no inventory exists
        }
    }

    // Getter for inventory
    public Inventory getInventory() {
        return inventory;
    }

    // Setter for inventory
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    // Method to check if the shop contains a specific item
    public boolean contains(AbstractItem item) {
        if (inventory != null && item != null) {
            return inventory.getItems().contains(item);
        }
        return false;
    }

}
