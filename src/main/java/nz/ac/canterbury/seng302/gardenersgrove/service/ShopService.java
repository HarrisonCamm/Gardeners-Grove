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

            Path clownPath = Paths.get(resourceLoader.getResource("classpath:static/images/clown.png").getURI());
            byte[] clownBytes = Files.readAllBytes(clownPath);
            Image clownImage = new Image(neoFabianImageBytes, "png", false);

            Path craftPath = Paths.get(resourceLoader.getResource("classpath:static/images/craft.png").getURI());
            byte[] craftBytes = Files.readAllBytes(craftPath);
            Image craftImage = new Image(craftBytes, "png", false);

            Path emmaLawPath = Paths.get(resourceLoader.getResource("classpath:static/images/emma-law.png").getURI());
            byte[] emmaLawBytes = Files.readAllBytes(emmaLawPath);
            Image emmaLawImage = new Image(emmaLawBytes, "png", false);

            Path gptPath = Paths.get(resourceLoader.getResource("classpath:static/images/gpt.png").getURI());
            byte[] gptBytes = Files.readAllBytes(gptPath);
            Image gptImage = new Image(gptBytes, "png", false);

            Path hamPath = Paths.get(resourceLoader.getResource("classpath:static/images/ham.png").getURI());
            byte[] hamBytes = Files.readAllBytes(hamPath);
            Image hamImage = new Image(hamBytes, "png", false);

            Path oofPath = Paths.get(resourceLoader.getResource("classpath:static/images/oof.png").getURI());
            byte[] oofBytes = Files.readAllBytes(oofPath);
            Image oofImage = new Image(oofBytes, "png", false);

            Path swagMorgPath = Paths.get(resourceLoader.getResource("classpath:static/images/swag-morg.png").getURI());
            byte[] swagMorgBytes = Files.readAllBytes(swagMorgPath);
            Image swagMorgImage = new Image(swagMorgBytes, "png", false);

            Path trollPath = Paths.get(resourceLoader.getResource("classpath:static/images/troll.png").getURI());
            byte[] trollBytes = Files.readAllBytes(trollPath);
            Image trollImage = new Image(trollBytes, "png", false);

            Path updogPath = Paths.get(resourceLoader.getResource("classpath:static/images/updog.png").getURI());
            byte[] updogBytes = Files.readAllBytes(updogPath);
            Image updogImage = new Image(updogBytes, "png", false);

            BadgeItem badge1 = new BadgeItem("Minecraft", 50, craftImage);
            BadgeItem badge2 = new BadgeItem("Emma's Word is Law", 50, emmaLawImage);
            BadgeItem badge3 = new BadgeItem("ChattyG", 25, gptImage);
            BadgeItem badge4 = new BadgeItem("Sad Hamster", 30, hamImage);
            BadgeItem badge5 = new BadgeItem("Oof", 35, oofImage);
            BadgeItem badge6 = new BadgeItem("Swag Morg", 40, swagMorgImage);
            BadgeItem badge7 = new BadgeItem("Troll Face", 45, trollImage);
            BadgeItem badge8 = new BadgeItem("Updog", 50, updogImage);
            BadgeItem badge9 = new BadgeItem("Tim Tam", 5000, timtamImage);
            BadgeItem badge10 = new BadgeItem("Vegemite", 2500, vegimiteImage);
            BadgeItem badge11 = new BadgeItem("Neo Fabian", 25, neoFabianImage);
            BadgeItem badge12 = new BadgeItem("Clown Faces", 50, clownImage);

            // Add items to the shop
            addItemToShop(badge1);
            addItemToShop(badge2);
            addItemToShop(badge3);
            addItemToShop(badge4);
            addItemToShop(badge5);
            addItemToShop(badge6);
            addItemToShop(badge7);
            addItemToShop(badge8);
            addItemToShop(badge9);
            addItemToShop(badge10);
            addItemToShop(badge11);

            // Create predefined profile pictures
            Path catFallImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/cat-fall.gif").getURI());
            byte[] catFallImageBytes = Files.readAllBytes(catFallImagePath);
            Image image1 = new Image(catFallImageBytes, "gif", false);
            ImageItem imageItem1 = new ImageItem("Cat Fall", 699, image1);

            Path catTypingImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/cat-typing.gif").getURI());
            byte[] catTypingImageBytes = Files.readAllBytes(catTypingImagePath);
            Image image2 = new Image(catTypingImageBytes, "gif", false);
            ImageItem imageItem2 = new ImageItem("Cat Typing",341, image2);

            Path fabianIntensifiesImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/fabian-intensifies.gif").getURI());
            byte[] fabianIntensifiesImageBytes = Files.readAllBytes(fabianIntensifiesImagePath);
            Image image3 = new Image(fabianIntensifiesImageBytes, "gif", false);
            ImageItem imageItem3 = new ImageItem("Fabian Intensifies",10, image3);

            Path catJamCryImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/cat-jam-cry.gif").getURI());
            byte[] catJamCryImageBytes = Files.readAllBytes(catJamCryImagePath);
            Image image4 = new Image(catJamCryImageBytes, "gif", false);
            ImageItem imageItem4 = new ImageItem("Cat Jam Cry",123, image4);

            Path disintegrateImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/disintegrate.gif").getURI());
            byte[] disintegrateImageBytes = Files.readAllBytes(disintegrateImagePath);
            Image image5 = new Image(disintegrateImageBytes, "gif", false);
            ImageItem imageItem5 = new ImageItem("Disintegrate",670, image5);

            Path elmoFireImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/elmo-fire.gif").getURI());
            byte[] elmoFireImageBytes = Files.readAllBytes(elmoFireImagePath);
            Image image6 = new Image(elmoFireImageBytes, "gif", false);
            ImageItem imageItem6 = new ImageItem("Elmo Fire",50, image6);

            Path harrisonIntensifiesImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/harrison-intensifies.gif").getURI());
            byte[] harrisonIntensifiesImageBytes = Files.readAllBytes(harrisonIntensifiesImagePath);
            Image image7 = new Image(harrisonIntensifiesImageBytes, "gif", false);
            ImageItem imageItem7 = new ImageItem("Harrison Intensifies",1250, image7);

            Path polarWalkImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/polar-walk.gif").getURI());
            byte[] polarWalkImageBytes = Files.readAllBytes(polarWalkImagePath);
            Image image8 = new Image(polarWalkImageBytes, "gif", false);
            ImageItem imageItem8 = new ImageItem("Polar Walk",300, image8);

            Path stickManImagePath = Paths.get(resourceLoader.getResource("classpath:static/images/stick_man.gif").getURI());
            byte[] stickManImageBytes = Files.readAllBytes(stickManImagePath);
            Image image9 = new Image(stickManImageBytes, "gif", false);
            ImageItem imageItem9 = new ImageItem("Stick Man",3000, image9);


            addItemToShop(imageItem1);
            addItemToShop(imageItem2);
            addItemToShop(imageItem3);
            addItemToShop(imageItem4);
            addItemToShop(imageItem5);
            addItemToShop(imageItem6);
            addItemToShop(imageItem7);
            addItemToShop(imageItem8);
            addItemToShop(imageItem9);
        }
    }

}
