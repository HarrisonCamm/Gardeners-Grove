package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.InventoryItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends CrudRepository<InventoryItem, Long> {
    List<InventoryItem> findAll();
    List<InventoryItem> findInventoryByOwner(User owner);

    InventoryItem findInventoryByOwnerAndItem(User owner, Item item);

}
