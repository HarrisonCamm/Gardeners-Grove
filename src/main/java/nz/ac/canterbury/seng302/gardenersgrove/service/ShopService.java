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
    private InventoryItemService inventoryService;

    // Injecting EntityManager
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ShopService(TransactionRepository transactionRepository,
                       UserRepository userRepository,
                       ItemRepository itemRepository,
                       ShopRepository shopRepository,
                       ResourceLoader resourceLoader,
                       UserService userService, InventoryItemService inventoryService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.shopRepository = shopRepository;
        this.resourceLoader = resourceLoader;
        this.userService = userService;
        this.inventoryService = inventoryService;
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
    public Shop getShop() {
        // Assumes we have only one Shop with a fixed ID (like 1L)
        return shopRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));
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
    public boolean purchaseItem(User user, Shop shop, Item item) {
        boolean successfulPurchase = false;
        InventoryItem itemInInventory = inventoryService.getInventory(user, item);
        if (shop.hasItem(item) && userService.canAfford(user, item)) {
            if (itemInInventory != null) {
                itemInInventory.setQuantity(itemInInventory.getQuantity() + 1);
            } else {
                // add item to user inventory
                InventoryItem inventory = new InventoryItem(user, item, 1);
                inventoryService.save(inventory);
            }

            userService.chargeBlooms(user, item.getPrice());
            userRepository.save(user);
            successfulPurchase= true;
        }
        return successfulPurchase;
    }

    @Transactional
    public void populateShopWithPredefinedItems() throws IOException {
        Shop shop = getShopInstance(entityManager);

        // Check if shop already has items to avoid duplicates
        if (shop.getAvailableItems().isEmpty()) {
            // Create predefined badges
            BadgeItem badge1 = new BadgeItem("Happy", 100, "😀");
            BadgeItem badge2 = new BadgeItem("Eggplant", 50, "\uD83C\uDF46");
            BadgeItem badge3 = new BadgeItem("Love", 25, "\uD83E\uDE77");
            BadgeItem badge4 = new BadgeItem("Diamond", 200, "\uD83D\uDC8E");


            // Add items to the shop
            addItemToShop(badge1);
            addItemToShop(badge2);
            addItemToShop(badge3);
            addItemToShop(badge4);

            // Create predefined profile pictures
            Path catFallImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/cat-fall.gif").getURI());
            byte[] catFallImageBytes = Files.readAllBytes(catFallImagePath);
            Image image1 = new Image(catFallImageBytes, "gif", false);
            ImageItem imageItem1 = new ImageItem("Cat Fall", 50, image1);

            Path catTypingImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/cat-typing.gif").getURI());
            byte[] catTypingImageBytes = Files.readAllBytes(catTypingImagePath);
            Image image2 = new Image(catTypingImageBytes, "gif", false);
            ImageItem imageItem2 = new ImageItem("Cat Typing",30, image2);

            Path fabianIntensifiesImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/fabian-intensifies.gif").getURI());
            byte[] fabianIntensifiesImageBytes = Files.readAllBytes(fabianIntensifiesImagePath);
            Image image3 = new Image(fabianIntensifiesImageBytes, "gif", false);
            ImageItem imageItem3 = new ImageItem("Fabian Intensifies",10, image3);

            Path catJamCryImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/cat-jam-cry.gif").getURI());
            byte[] catJamCryImageBytes = Files.readAllBytes(catJamCryImagePath);
            Image image4 = new Image(catJamCryImageBytes, "gif", false);
            ImageItem imageItem4 = new ImageItem("Cat Jam Cry",20, image4);

            Path disintegrateImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/disintegrate.gif").getURI());
            byte[] disintegrateImageBytes = Files.readAllBytes(disintegrateImagePath);
            Image image5 = new Image(disintegrateImageBytes, "gif", false);
            ImageItem imageItem5 = new ImageItem("Disintegrate",40, image5);

            Path elmoFireImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/elmo-fire.gif").getURI());
            byte[] elmoFireImageBytes = Files.readAllBytes(elmoFireImagePath);
            Image image6 = new Image(elmoFireImageBytes, "gif", false);
            ImageItem imageItem6 = new ImageItem("Elmo Fire",20, image6);

            Path harrisonIntensifiesImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/harrison-intensifies.gif").getURI());
            byte[] harrisonIntensifiesImageBytes = Files.readAllBytes(harrisonIntensifiesImagePath);
            Image image7 = new Image(harrisonIntensifiesImageBytes, "gif", false);
            ImageItem imageItem7 = new ImageItem("Harrison Intensifies",20, image7);

            Path polarWalkImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/polar-walk.gif").getURI());
            byte[] polarWalkImageBytes = Files.readAllBytes(polarWalkImagePath);
            Image image8 = new Image(polarWalkImageBytes, "gif", false);
            ImageItem imageItem8 = new ImageItem("Polar Walk",20, image8);

            Path scrumMasterHarrisonImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/scrum_master_harrison.gif").getURI());
            byte[] scrumMasterHarrisonImageBytes = Files.readAllBytes(scrumMasterHarrisonImagePath);
            Image image9 = new Image(scrumMasterHarrisonImageBytes, "gif", false);
            ImageItem imageItem9 = new ImageItem("Scrum Master Harrison",20, image9);

            Path stickManImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/stick_man.gif").getURI());
            byte[] stickManImageBytes = Files.readAllBytes(stickManImagePath);
            Image image10 = new Image(stickManImageBytes, "gif", false);
            ImageItem imageItem10 = new ImageItem("Stick Man",20, image10);


            addItemToShop(imageItem1);
            addItemToShop(imageItem2);
            addItemToShop(imageItem3);
            addItemToShop(imageItem4);
            addItemToShop(imageItem5);
            addItemToShop(imageItem6);
            addItemToShop(imageItem7);
            addItemToShop(imageItem8);
            addItemToShop(imageItem9);
            addItemToShop(imageItem10);
        }
    }

}
