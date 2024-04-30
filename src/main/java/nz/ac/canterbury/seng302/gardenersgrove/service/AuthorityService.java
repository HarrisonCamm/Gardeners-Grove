package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Authority;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityService {
    private final AuthorityRepository authorityRepository;

    @Autowired
    public AuthorityService(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    public void deleteByUser(User user) {
        authorityRepository.deleteByUser(user);
    }

    public void delete(Authority authority) { authorityRepository.delete(authority);}

    public List<Authority> findByRole(String role) {
        return authorityRepository.findByRole(role);
    }

    public Authority findById(Long id) { return authorityRepository.findByAuthorityId(id);}

}
