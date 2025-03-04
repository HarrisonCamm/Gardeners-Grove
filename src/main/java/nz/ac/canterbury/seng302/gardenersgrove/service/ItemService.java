package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void saveItem(Item item) {
        itemRepository.save(item);
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

    public List<Item> getImageItemsByImageId(Long imageId) {
        return itemRepository.findImageItemsByImageId(imageId);
    }

}