package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemService {
    private ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
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

//    public Item getItemByName(String name) {
//        return itemRepository.findByName(name).orElse(null);
//    }

    public boolean itemExists(String name) {
//        return itemRepository.findByName(name).isPresent();
        return true;
    }


//
//    public Iterable<Item> getAllItems() {
//        return itemRepository.findAll();
//    }
//
//    public Iterable<Item> getBadges() {
//        return itemRepository.findBadges();
//    }
//
//    public Iterable<Item> getImages() {
//        return itemRepository.findImages();
//    }
//
//    public Iterable<Item> getEquipable() {
//        return itemRepository.findIsEquipable(true);
//    }
//
//    public Iterable<Item> getEquippedByOwner(User owner) {
//        return itemRepository.findIsEquippedByOwner(owner);
//    }
//
//    public Iterable<Item> getEquipableByOwner(User owner) {
//        return itemRepository.findIsEquipableByOwner(owner);
//    }
//
//    public Iterable<Item> getBadgesByOwner(User owner) {
//        return itemRepository.findBadgesByOwner(owner);
//    }
//
//    public Iterable<Item> getImagesByOwner(User owner) {
//        return itemRepository.findImagesByOwner(owner);
//    }
//
//    public Iterable<Item> getItemsByOwner(User owner) {
//        return itemRepository.findByOwner(owner);
//    }

}
