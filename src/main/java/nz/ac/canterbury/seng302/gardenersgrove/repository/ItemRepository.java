package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;



@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {

    // Find items by their name (applies to any subclass of Item)
    Optional<Item> findByName(String name);

    @Query("SELECT i FROM Item i WHERE i.itemType = 'badge'")
    List<Item> findBadges();

    @Query("SELECT i FROM Item i WHERE i.itemType = 'image'")
    List<Item> findImages();


}
