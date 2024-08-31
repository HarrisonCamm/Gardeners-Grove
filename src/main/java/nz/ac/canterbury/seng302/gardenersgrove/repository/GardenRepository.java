package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GardenRepository extends CrudRepository<Garden, Long> {
    Optional<Garden> findById(long id);
    List<Garden> findAll();
    List<Garden> findByOwnerUserId(Long ownerUserId);

    @Query("SELECT g FROM Garden g WHERE :tagId IN (SELECT t.id FROM g.tags t)")
    List<Garden> findGardensByTagId(Long tagId);

    @Query("SELECT g FROM Garden g WHERE g.isPublic = TRUE")
    List<Garden> findPublicGardens();

    @Query("SELECT g FROM Garden g JOIN Plant p ON p.garden = g WHERE g.isPublic = TRUE AND (g.name LIKE %:search% OR p.name LIKE %:search%)")
    List<Garden> findPublicGardensBySearch(String search);

}
