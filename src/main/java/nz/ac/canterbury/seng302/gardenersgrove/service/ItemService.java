package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ItemRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public void deleteItem(Item item) {
        itemRepository.delete(item);
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElse(null);
    }

    public Item getItemByName(String name) {
        return itemRepository.findByName(name).orElse(null);
    }

    public boolean itemExists(String name) {
        return itemRepository.findByName(name).isPresent();
    }

    public Iterable<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> getBadges() {
        return itemRepository.findBadges();
    }

    public List<Item> getImages() {
        return itemRepository.findImages();
    }

    public String purchaseItem(Long itemId, Long userId) {
        // Find the item and user by their IDs
        Item item = itemRepository.findById(itemId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);


        if (item == null) {
            return "Item not found.";
        }

        if (user == null) {
            return "User not found.";
        }

        // Check if the user has enough balance
        if (user.getBloomBalance() >= item.getPrice()) {
            // Deduct the cost from the user's balance
            user.setBloomBalance(user.getBloomBalance() - item.getPrice());

            // Set the owner of the item to the current user

            // Save the updated item and user entities
            itemRepository.save(item);
            userRepository.save(user);

            return "Purchase successful";
        } else {
            return "Insufficient Bloom balance";
        }
    }
}