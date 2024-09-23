package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Item;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Find items by their name (applies to any subclass of Item)
    List<Item> findByName(String name);

    // Find all items by owner ID (works for any type of item)
    List<Item> findByOwner_UserId(Long userId);
}



//@Repository
//public interface ItemRepository extends CrudRepository<Item, Long> {
//    @NotNull
//    Optional<Item> findById(@NotNull Long id);
//
//    @NotNull
//    List<Item> findAll();
//
//    Optional<Item> findByName(String name);
//
//    List<Item> findByOwner(User owner);
//
//    @Query("SELECT i FROM Item i WHERE i.itemType = 'badge'")
//    List<Item> findBadges();
//
//    List<Item> findBadgesByOwner(User owner);
//
//    @Query("SELECT i FROM Item i WHERE i.itemType = 'image'")
//    List<Item> findImages();
//
//    List<Item> findImagesByOwner(User owner);
//
//    @Query("SELECT i FROM Item i WHERE i.isEquipable = :isEquipable")
//    List<Item> findIsEquipable(Boolean isEquipable);
//
//    List<Item> findIsEquipableByOwner(User owner);
//
//    List<Item> findIsEquippedByOwner(User owner);
//
//}
