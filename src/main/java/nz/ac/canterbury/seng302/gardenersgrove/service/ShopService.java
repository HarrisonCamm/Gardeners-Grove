package nz.ac.canterbury.seng302.gardenersgrove.service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Service
public class ShopService {
    private static Shop shopInstance;
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private ShopRepository shopRepository;
    private ItemRepository itemRepository;
    private ResourceLoader resourceLoader;
    private UserService userService;

    // Injecting EntityManager
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ShopService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository,
                              ShopRepository shopRepository,
                              ResourceLoader resourceLoader,
                              UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.shopRepository = shopRepository;
        this.resourceLoader = resourceLoader;
        this.userService = userService;
    }

    public static Shop getShopInstance(EntityManager em) {
        if (shopInstance == null) {
            // Assuming the shop is persisted once in the database and retrieved from there.
            shopInstance = em.find(Shop.class, 1L);  // ID 1 for the single shop
            if (shopInstance == null) {
                shopInstance = new Shop();
                em.persist(shopInstance);  // Persist the shop for the first time
            }
        }
        return shopInstance;
    }

    @Transactional
    public void addItemToShop(Item item) {
        Shop shop = getShopInstance(entityManager);
        shop.getAvailableItems().add(item);
        itemRepository.save(item); // Save item first if necessary
        shopRepository.save(shop);
    }
    @Transactional
    public void removeItemFromShop(Long itemId) {
        Shop shop = getShopInstance(entityManager);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        shop.removeItem(item);
        itemRepository.delete(item);
        shopRepository.save(shop);
    }

    @Transactional
    public Set<Item> getItemsInShop() {
        Shop shop = getShopInstance(entityManager);
        return shop.getAvailableItems();
    }

    @Transactional
    public void purchaseItem(User user, Item item) {
        Shop shop = getShopInstance(entityManager);

        if (shop.hasItem(item) && userService.canAfford(user, item)) {

            // add item to user inventory
            user.addItem(item);

            userService.chargeBlooms(user, item.getPrice());
            userRepository.save(user);
        }
    }

    @Transactional
    public void populateShopWithPredefinedItems() throws IOException {
        Shop shop = getShopInstance(entityManager);

        // Check if shop already has items to avoid duplicates
        if (shop.getAvailableItems().isEmpty()) {
            // Create predefined badges
            BadgeItem badge1 = new BadgeItem("Happy", 100, "😀", 1);
            BadgeItem badge2 = new BadgeItem("Eggplant", 50, "\uD83C\uDF46", 1);
            BadgeItem badge3 = new BadgeItem("Love", 25, "\uD83E\uDE77", 1);
            BadgeItem badge4 = new BadgeItem("Diamond", 200, "\uD83D\uDC8E", 1);


            // Add items to the shop
            addItemToShop(badge1);
            addItemToShop(badge2);
            addItemToShop(badge3);
            addItemToShop(badge4);

            // Create predefined profile pictures
            Path catFallImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/cat-fall.gif").getURI());
            byte[] catFallImageBytes = Files.readAllBytes(catFallImagePath);
            Image image1 = new Image(catFallImageBytes, "gif", false);
            ImageItem imageItem1 = new ImageItem("Cat Fall", 50, image1, 1);

            Path catTypingImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/cat-typing.gif").getURI());
            byte[] catTypingImageBytes = Files.readAllBytes(catTypingImagePath);
            Image image2 = new Image(catTypingImageBytes, "gif", false);
            ImageItem imageItem2 = new ImageItem("Cat Typing",30, image2, 1);

            Path fabianIntensifiesImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/fabian-intensifies.gif").getURI());
            byte[] fabianIntensifiesImageBytes = Files.readAllBytes(fabianIntensifiesImagePath);
            Image image3 = new Image(fabianIntensifiesImageBytes, "gif", false);
            ImageItem imageItem3 = new ImageItem("Fabian Intensifies",10, image3, 1);

            addItemToShop(imageItem1);
            addItemToShop(imageItem2);
            addItemToShop(imageItem3);
        }
    }

}
