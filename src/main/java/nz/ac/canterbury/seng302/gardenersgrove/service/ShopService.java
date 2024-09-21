package nz.ac.canterbury.seng302.gardenersgrove.service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Shop;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
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
    private PlantRepository plantRepository;
    private ItemRepository itemRepository;
    private ShopRepository shopRepository;

    // Injecting EntityManager
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ShopService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              PlantRepository plantRepository, ItemRepository itemRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.plantRepository = plantRepository;
        this.itemRepository = itemRepository;
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


    public void addItemToShop(Item item) {
        Shop shop = getShopInstance(entityManager);
        shop.getAvailableItems().add(item);
        itemRepository.save(item); // Save item first if necessary
        shopRepository.save(shop);
    }

    public void removeItemFromShop(Long itemId) {
        Shop shop = getShopInstance(entityManager);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        shop.removeItem(item);
        itemRepository.delete(item);
        shopRepository.save(shop);
    }
    public Set<Item> getItemsInShop() {
        Shop shop = getShopInstance(entityManager);
        return shop.getAvailableItems();
    }
    public void purchaseItem(User user, Shop shop, Item item) {
        if (shop.hasItem(item) && user.canAfford(item)) {
            shop.removeItem(item);
            user.addItem(item, 1);
            user.decreaseBloomBalance(item.getPrice());
        }
    }
}
