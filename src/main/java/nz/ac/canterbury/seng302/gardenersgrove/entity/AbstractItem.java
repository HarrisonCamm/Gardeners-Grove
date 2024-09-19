package nz.ac.canterbury.seng302.gardenersgrove.entity;

// Abstract class for shared properties
public abstract class AbstractItem implements Purchasable {
    private String name;
    private Integer price;
    private boolean isSellable;

    public AbstractItem(String name, Integer price, boolean isSellable) {
        this.name = name;
        this.price = price;
        this.isSellable = isSellable;
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