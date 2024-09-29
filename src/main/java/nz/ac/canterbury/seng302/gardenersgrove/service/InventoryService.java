package nz.ac.canterbury.seng302.gardenersgrove.service;


import nz.ac.canterbury.seng302.gardenersgrove.entity.Inventory;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.entity.UserRelationship;
import nz.ac.canterbury.seng302.gardenersgrove.repository.InventoryRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public void save(Inventory inventory) {
        inventoryRepository.save(inventory);
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
//    public void remove(UserRelationship userRelationship) {
//        userRelationshipRepository.delete(userRelationship);
//    }
//
//    public void removeAll() {
//        userRelationshipRepository.deleteAll();
//    }
}
