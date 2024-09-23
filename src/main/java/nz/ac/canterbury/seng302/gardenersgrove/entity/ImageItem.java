package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("image")
public class ImageItem extends Item {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Image image;

    protected ImageItem() {
    }

    public ImageItem(String name, int price, Image image, Integer quantity) {
        super(name, price, quantity);
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

}
