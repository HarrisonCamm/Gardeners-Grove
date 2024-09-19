package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

// Abstract class for shared properties
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public abstract class AbstractItem implements Purchasable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private boolean isSellable;

    public AbstractItem() {
    }

    public AbstractItem(String name, Integer price, boolean isSellable) {
        this.name = name;
        this.price = price;
        this.isSellable = isSellable;
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

}