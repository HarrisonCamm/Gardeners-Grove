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
    private List<AbstractItem> items;

    public Inventory() {
        // Default constructor
    }

    public Long getId() {
        return id;
    }

    public List<AbstractItem> getItems() {
        return items;
    }

    public void addItem(AbstractItem item) {
        items.add(item);
    }

    public void removeItem(AbstractItem item) {
        items.remove(item);
    }

    public boolean contains(AbstractItem item) {
        return items.contains(item);
    }

    public void setItems(List<AbstractItem> items) {
        this.items = items;
    }

}

