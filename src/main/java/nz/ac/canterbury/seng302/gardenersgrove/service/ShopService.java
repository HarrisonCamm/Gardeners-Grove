package nz.ac.canterbury.seng302.gardenersgrove.service;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Shop;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShopService {
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private PlantService Plant;
    private PlantRepository plantRepository;
    private ItemRepository itemRepository;

    private ShopRepository shopRepository;

    @Autowired
    public ShopService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              PlantRepository plantRepository, ItemRepository itemRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.plantRepository = plantRepository;
        this.itemRepository = itemRepository;
    }

    public void addItemToShop(Long shopId, Item item) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new IllegalArgumentException("Shop not found"));
        shop.getItems().add(item);
        itemRepository.save(item); // Save item first if necessary
        shopRepository.save(shop);
    }

    public void removeItemFromShop(Long shopId, Long itemId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new IllegalArgumentException("Shop not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        shop.getItems().remove(item);
        itemRepository.delete(item);
        shopRepository.save(shop);
    }
    public List<Item> getItemsInShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new IllegalArgumentException("Shop not found"));
        return shop.getItems();
    }
    public void purchaseItem(User user, Shop shop, Item item) {
        if (shop.getInventory().contains(item) && user.canAfford(item)) {
            shop.getInventory().removeItem(item);
            user.getInventory().addItem(item);
            user.decreaseBloomBalance(item.getPrice());
        }
    }
}
