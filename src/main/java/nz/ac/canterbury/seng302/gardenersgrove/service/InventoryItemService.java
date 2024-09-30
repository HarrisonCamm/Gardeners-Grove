package nz.ac.canterbury.seng302.gardenersgrove.service;


import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.repository.InventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InventoryItemService {
    private final InventoryItemRepository inventoryRepository;
    private final ImageService imageService;
    private final UserService userService;

    @Autowired
    public InventoryItemService(InventoryItemRepository inventoryRepository, ImageService imageService, UserService userService) {
        this.inventoryRepository = inventoryRepository;
        this.imageService = imageService;
        this.userService = userService;
    }

    /**
     * Save the inventory, if the inventory already exists when trying to save a brand-new inventory,
     *      then increment the quantity
     * @param inventory the inventory to save
     */
    public void save(InventoryItem inventory) {
        InventoryItem existing = null;

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

    public List<InventoryItem> getAllInventory() {
        return inventoryRepository.findAll();
    }
    public List<InventoryItem> getUserInventory(User owner) {
        return inventoryRepository.findInventoryByOwner(owner);
    }

    public InventoryItem getInventory(User owner, Item item) {
        return inventoryRepository.findInventoryByOwnerAndItem(owner, item);
    }

    /**
     * Get the items in the inventory of a user
     * @param owner The user to get the inventory of
     * @return A list of items in the inventory
     */
    public List<Map.Entry<Item,Integer>> getItems(User owner) {
        List<InventoryItem> inventoryItems = getUserInventory(owner);
        // Convert the inventory items to a list of items
        List<Map.Entry<Item,Integer>> items = new ArrayList<>();
        for (InventoryItem inventoryItem: inventoryItems) {
            // Create a map entry of the item and its quantity
            Map.Entry<Item,Integer> item =new AbstractMap.SimpleEntry<>(inventoryItem.getItem(), inventoryItem.getQuantity());
            items.add(item);
        }
        return items;
    }

    public void deleteInventoryItem(InventoryItem inventoryItem) {
        inventoryRepository.delete(inventoryItem);
    }

    public void removeInventoryItem(InventoryItem inventoryItem, User currentUser) {
        Integer quantity = inventoryItem.getQuantity();
        if (quantity > 1) {
            inventoryItem.setQuantity(quantity - 1);
            save(inventoryItem);
        } else {
            if (inventoryItem.getItem() instanceof BadgeItem) {
                currentUser.setAppliedBadge(null);
                userService.saveUser(currentUser);
            }
            if (inventoryItem.getItem() instanceof ImageItem) {
                Optional<Image> imageOpt = imageService.findImage(currentUser.getUploadedImageId());
                if (imageOpt.isPresent()) {
                    currentUser.setImage(imageOpt.get());
                    userService.saveUser(currentUser);
                }
            }
            deleteInventoryItem(inventoryItem);
        }
    }
    public InventoryItem getInventoryByOwnerIdAndImageId(Long ownerId, Long imageId) {
        return inventoryRepository.findInventoryByOwnerIdAndImageId(ownerId, imageId);
    }

    public InventoryItem getInventoryByOwnerIdAndItemId(Long ownerId, Long itemId) {
        return inventoryRepository.findInventoryByOwnerIdAndItemId(ownerId, itemId);
    }

}
