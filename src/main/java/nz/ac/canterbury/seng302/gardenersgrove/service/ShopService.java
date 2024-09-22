package nz.ac.canterbury.seng302.gardenersgrove.service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ShopService {
    private static Shop shopInstance;
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private PlantService Plant;

    private ShopRepository shopRepository;

    private ItemRepository itemRepository;

    // Injecting EntityManager
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ShopService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              PlantRepository plantRepository, ItemRepository itemRepository,
                              ShopRepository shopRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.shopRepository = shopRepository;
    }

    @Transactional
    public Shop getShopInstance(EntityManager em) {
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
    public void purchaseItem(User user, Shop shop, Item item) {
        if (shop.hasItem(item) && user.canAfford(item)) {
            shop.removeItem(item);
            user.addItem(item, 1);
            user.decreaseBloomBalance(item.getPrice());
        }
    }

    @Transactional
    public void populateShopWithPredefinedItems() {
        Shop shop = getShopInstance(entityManager);

        // Check if shop already has items to avoid duplicates
        if (shop.getAvailableItems().isEmpty()) {
            // Create predefined badges
            Badge badge1 = new Badge("Happy Badge", 100, "😀");
            Badge badge2 = new Badge("Cucumber Badge", 50, "\uD83C\uDF46");
            Badge badge3 = new Badge("Love", 25, "\uD83E\uDE77");
            Badge badge4 = new Badge("Diamond", 200, "\uD83D\uDC8E");


            // Add items to the shop
            addItemToShop(badge1);
            addItemToShop(badge2);
            addItemToShop(badge3);
            addItemToShop(badge4);

        }
    }

}
