package nz.ac.canterbury.seng302.gardenersgrove.repository;

        import nz.ac.canterbury.seng302.gardenersgrove.entity.Authority;
        import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
        import org.springframework.data.jpa.repository.Modifying;
        import org.springframework.data.jpa.repository.Query;
        import org.springframework.data.repository.CrudRepository;
        import org.springframework.stereotype.Repository;
        import org.springframework.transaction.annotation.Transactional;

        import java.util.List;
        import java.util.Optional;

@Repository
public interface AuthorityRepository extends CrudRepository<Authority, User> {

    Authority findByAuthorityId(Long id);
    Optional<Authority> findByUser(User user);
    List<Authority> findByRole(String role);

    @Transactional
    @Modifying
    @Query("DELETE FROM Authority a WHERE a.user = :user")
    void deleteByUser(User user);

}
