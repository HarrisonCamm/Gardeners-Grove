package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

//@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "item_type", discriminatorType = DiscriminatorType.STRING)
//public abstract class Item {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    private String name;
//
//    @Column(name = "item_type", insertable = false, updatable = false)
//    private String itemType;
//
//    @Column(nullable = false)
//    private Boolean isSellable;
//
//    @Column(nullable = false)
//    private Boolean isEquipable;
//
//    @Column(nullable = false)
//    private Boolean isEquipped;
//
//    @ManyToOne
//    @JoinColumn
//    private User owner;
//
//    @Column(nullable = false)
//    private Integer price;
//
//    protected Item() {
//
//    }
//
//    public Item(String name, boolean isEquipable, int price) {
//        this.name = name;
//        this.isEquipable = isEquipable;
//        this.price = price;
//        this.isSellable = true;
//        this.isEquipped = false;
//
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getItemType() {
//        return itemType;
//    }
//
//    public Boolean getIsSellable() {
//        return isSellable;
//    }
//
//    public void setIsSellable(Boolean sellable) {
//        this.isSellable = sellable;
//    }
//
//    public Boolean getIsEquipable() {
//        return isEquipable;
//    }
//
//    public Boolean getIsEquipped() {
//        return isEquipped;
//    }
//
//    public void setIsEquipped(Boolean equipped) {
//        this.isEquipped = equipped;
//    }
//
//    public User getOwner() {
//        return owner;
//    }
//
//    public void setOwner(User owner) {
//        this.owner = owner;
//    }
//
//    public Integer getPrice() {
//        return price;
//    }
//
//    public void setPrice(Integer price) {
//        this.price = price;
//    }
//
//}


// Abstract class for shared properties
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public abstract class Item implements Purchasable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer price;


    public Item() {
    }

    public Item(String name, Integer price) {
        this.name = name;
        this.price = price;
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








