package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.TemporaryUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemporaryUserRepository extends CrudRepository<TemporaryUser, Long>{

    @Override
    Optional<TemporaryUser> findById(Long id);

    void deleteById(Long id);
}
