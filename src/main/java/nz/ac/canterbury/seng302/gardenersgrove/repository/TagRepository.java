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

    List<Tag> findAll();

    Tag save(Tag tag);

    Optional<Tag> getTagById(Long id);

    Tag getTagByName(String name);
}
