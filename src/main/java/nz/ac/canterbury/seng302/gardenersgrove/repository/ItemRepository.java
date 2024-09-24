package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Find items by their name (applies to any subclass of Item)
    List<Item> findByName(String name);

    // Find all items by owner ID (works for any type of item)
    List<Item> findByOwnerUserId(Long userId);
}
