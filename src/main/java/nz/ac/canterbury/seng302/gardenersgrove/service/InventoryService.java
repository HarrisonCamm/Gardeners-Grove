package nz.ac.canterbury.seng302.gardenersgrove.service;


import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Save the inventory, if the inventory already exists when trying to save a brand-new inventory,
     *      then increment the quantity
     * @param inventory the inventory to save
     */
    public void save(Inventory inventory) {
        Inventory existing = null;

        // If the inventory is a new inventory, check if the inventory already exists and not unique
        if (inventory.getId() == null) {
            existing = inventoryRepository.findInventoryByOwnerIdAndItemId(
                    inventory.getOwner().getUserId(), inventory.getItem().getId());
        }

        // If the inventory already exists, increment the quantity
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + inventory.getQuantity());
            inventoryRepository.save(existing);
        } else {
            inventoryRepository.save(inventory);
        }
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }
    public List<Inventory> getUserInventory(User owner) {
        return inventoryRepository.findInventoryByOwner(owner);
    }

    public Inventory getInventory(User owner, Item item) {
        return inventoryRepository.findInventoryByOwnerAndItem(owner, item);
    }

    public List<Map.Entry<Item,Integer>> getItems(User owner) {
        List<Inventory> inventoryItems = getUserInventory(owner);
        List<Map.Entry<Item,Integer>> items = new ArrayList<>();
        for (Inventory inventoryItem: inventoryItems) {
            Map.Entry<Item,Integer> item =new AbstractMap.SimpleEntry<>(inventoryItem.getItem(), inventoryItem.getQuantity());
            items.add(item);
        }
        return items;
    }

    public Inventory getInventoryByOwnerIdAndImageId(Long ownerId, Long imageId) {
        return inventoryRepository.findInventoryByOwnerIdAndImageId(ownerId, imageId);
    }

}
