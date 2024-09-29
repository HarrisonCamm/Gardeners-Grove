package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"owner", "item"})})
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "item")
    private Item item;

    @Column(name = "quantity")
    private Integer quantity;

    public Inventory() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public User getOwner() {
        return owner;
    }
    public Item getItem() {
        return item;
    }

    public Inventory(User owner, Item item, Integer quantity) {
        this.owner = owner;
        this.item = item;
        this.quantity = quantity;
    }

}
