package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

import java.util.Objects;

// Abstract class for shared properties
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "item_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Item implements Purchasable, Equipable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_type", insertable = false, updatable = false)
    private String itemType;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean isEquipped;

    @Column(nullable = false)
    private Integer price;


    protected Item() {
    }

    protected Item(String name, Integer price) {
        this.name = name;
        this.price = price;
        this.isEquipped = false;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Integer getPrice() {
        return price;
    }

    @Override
    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public boolean isEquipped() {
        return isEquipped;
    }

    @Override
    public void setEquipped(boolean equipped) {
        this.isEquipped = equipped;
    }

    @Override
    public boolean equals(Object item) {
        if (!(item instanceof Item)) {
            return false;
        }
        return this.name.equals(((Item) item).name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


}








