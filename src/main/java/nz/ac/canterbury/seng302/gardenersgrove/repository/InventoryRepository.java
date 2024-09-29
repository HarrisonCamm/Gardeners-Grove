package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Inventory;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends CrudRepository<Inventory, Long> {
    List<Inventory> findAll();
//    Optional<Inventory> findUserRelationshipByReceiverAndSender(User receiver, User sender);
    List<Inventory> findInventoryByOwner(User owner);

    Inventory findInventoryByOwnerAndItem(User owner, Item item);

}
