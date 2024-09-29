package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.BadgeItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.ImageItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ItemRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ItemService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ShopService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemServiceTests {

    @Autowired
    private ItemRepository itemRepository;
    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;
    @MockBean
    private ImageService imageService;
    @MockBean
    private ShopService shopService;

    @Autowired
    private ResourceLoader resourceLoader;

    private BadgeItem badge1;
    private BadgeItem badge2;
    private BadgeItem badge3;
    private ImageItem catFallImage;
    private ImageItem catTypingImage;
    private ImageItem fabianIntensifiesImage;

    @BeforeEach
    void setUp() {
        itemService = new ItemService(itemRepository);

        try {
            Path catFallImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/cat-fall.gif").getURI());
            Image image1 = new Image(Files.readAllBytes(catFallImagePath), "gif", false);
            catFallImage = new ImageItem("Cat Fall1", 5000, image1, 1);

            Path catTypingImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/cat-typing.gif").getURI());
            Image image2 = new Image(Files.readAllBytes(catTypingImagePath), "gif", false);
            catTypingImage = new ImageItem("Cat Typing1", 6000, image2, 1);

            Path fabianIntensifiesImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/fabian-intensifies.gif").getURI());
            Image image3 = new Image(Files.readAllBytes(fabianIntensifiesImagePath), "gif", false);
            fabianIntensifiesImage = new ImageItem("Fabian Intensifies1", 7000, image3, 1);

            // Create predefined profile pictures
            Path timtamImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/timtam.png").getURI());
            byte[] timtamImageBytes = Files.readAllBytes(timtamImagePath);
            Image timtamImage = new Image(timtamImageBytes, "png", false);

            Path vegimiteImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/vegemite.png").getURI());
            byte[] vegimiteImageBytes = Files.readAllBytes(vegimiteImagePath);
            Image vegimiteImage = new Image(vegimiteImageBytes, "png", false);

            Path neoFabianImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/neo_fabian.png").getURI());
            byte[] neoFabianImageBytes = Files.readAllBytes(neoFabianImagePath);
            Image neoFabianImage = new Image(neoFabianImageBytes, "png", false);

            // Initialize class-level variables
            badge1 = new BadgeItem("Tim Tam", 100, timtamImage, 1);
            badge2 = new BadgeItem("Vegemite", 50, vegimiteImage, 1);
            badge3 = new BadgeItem("Neo Fabian", 25, neoFabianImage, 1);

            itemService.saveItem(badge1);
            itemService.saveItem(badge2);
            itemService.saveItem(badge3);

            itemService.saveItem(catFallImage);
            itemService.saveItem(catTypingImage);
            itemService.saveItem(fabianIntensifiesImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void GetItemByName_ReturnsCorrectItem() {
        BadgeItem result = (BadgeItem) itemService.getItemByName("Tim Tam");
        BadgeItem result2 = (BadgeItem) itemService.getItemByName("Vegemite");
        BadgeItem result3 = (BadgeItem) itemService.getItemByName("Neo Fabian");
        ImageItem result5 = (ImageItem) itemService.getItemByName("Cat Fall1");
        ImageItem result6 = (ImageItem) itemService.getItemByName("Cat Typing1");
        ImageItem result7 = (ImageItem) itemService.getItemByName("Fabian Intensifies1");
        assertEquals(badge1, result);
        assertEquals(badge2, result2);
        assertEquals(badge3, result3);
        assertEquals(catFallImage, result5);
        assertEquals(catTypingImage, result6);
        assertEquals(fabianIntensifiesImage, result7);
    }

    @Test
    void ItemExists_ReturnsTrueIfItemExists() {
        boolean result = itemService.itemExists("Tim Tam");
        boolean result2 = itemService.itemExists("Vegemite");
        boolean result3 = itemService.itemExists("Neo Fabian");
        boolean result5 = itemService.itemExists("Cat Fall1");
        boolean result6 = itemService.itemExists("Cat Typing1");
        boolean result7 = itemService.itemExists("Fabian Intensifies1");
        assertTrue(result);
        assertTrue(result2);
        assertTrue(result3);
        assertTrue(result5);
        assertTrue(result6);
        assertTrue(result7);
    }

    @Test
    void GetAllItems_ReturnsAllItems() {
        assertEquals(6, itemService.getAllItems().spliterator().getExactSizeIfKnown());
    }

    @Test
    void GetBadges_ReturnsAllBadges() {
        List<Item> badges = itemService.getBadges();
        badges.forEach(badge -> assertInstanceOf(BadgeItem.class, badge));
        assertEquals(3, itemService.getBadges().size());
        assertTrue(badges.contains(badge1));
        assertTrue(badges.contains(badge2));
        assertTrue(badges.contains(badge3));
    }

    @Test
    void GetImages_ReturnsAllImages() {
        List<Item> images = itemService.getImages();
        images.forEach(image -> assertInstanceOf(ImageItem.class, image));
        assertEquals(3, itemService.getImages().size());
        assertTrue(images.contains(catFallImage));
        assertTrue(images.contains(catTypingImage));
        assertTrue(images.contains(fabianIntensifiesImage));
    }
}
