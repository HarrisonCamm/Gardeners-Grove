package nz.ac.canterbury.seng302.gardenersgrove.repository;


import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    @Query("SELECT DISTINCT t FROM Tag t")
    List<Tag> findAll();

    @Query("SELECT t FROM Tag t WHERE t.gardenId = ?1")
    List<Tag> findByGardenId(@Param("gardenId") Long gardenId);

    Tag save(Tag tag);

    @Query("SELECT t FROM Tag t WHERE t.id = ?1")
    Optional<Tag> getTag(@Param("id") Long id);

}
