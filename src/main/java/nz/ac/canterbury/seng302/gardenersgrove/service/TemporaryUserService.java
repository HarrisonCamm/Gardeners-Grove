package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.TemporaryUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TemporaryUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemporaryUserService {
    private TemporaryUserRepository temporaryUserRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public TemporaryUserService(TemporaryUserRepository temporaryUserRepository,
                                BCryptPasswordEncoder passwordEncoder) {
        this.temporaryUserRepository = temporaryUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Adds User entity into the database.
     *
     * @param tempUser the User entity to be added
     * @return the User entity
     */
    public TemporaryUser addTempUser(TemporaryUser tempUser) {
        // Encodes the users password
        String encodedPassword = passwordEncoder.encode(tempUser.getPassword());
        // Updates users password to the encrypted one
        tempUser.setPassword(encodedPassword);
        return temporaryUserRepository.save(tempUser);
    }

    public void deleteTemporaryUserById(Long id) {
        temporaryUserRepository.deleteById(id);
    }

    public TemporaryUser getUserById(Long id) {
        return temporaryUserRepository.findById(id).orElse(null);
    }

    public long getTableSize() {
        return temporaryUserRepository.count();
    }
}
