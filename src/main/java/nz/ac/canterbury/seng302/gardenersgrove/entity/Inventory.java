package nz.ac.canterbury.seng302.gardenersgrove.entity;


import jakarta.persistence.*;

import java.util.List;

@Entity
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private List<Item> items;

    @OneToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;  // Add this field for the bidirectional relationship




    public Inventory() {
        // Default constructor
    }

    public Long getId() {
        return id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public boolean contains(Item item) {
        return items.contains(item);
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }




}

