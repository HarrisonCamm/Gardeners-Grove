package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {

    @Override
    Optional<Image> findById(Long id);

    List<Image> findAll();

    @Transactional
    @Modifying
    @Query("DELETE FROM Image i WHERE i.expiryDate <> null AND i.expiryDate < :now")
    void deleteAllExpiredTemporaries(LocalDateTime now);

}

