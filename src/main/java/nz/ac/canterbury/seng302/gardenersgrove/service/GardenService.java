package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for FormResults, defined by the @link{Service} annotation.
 * This class links automatically with @link{FormRepository}, see the @link{Autowired} annotation below
 */
@Service
public class GardenService {
    private GardenRepository gardenRepository;

//    @Autowired
    public GardenService(GardenRepository gardenRepository) {
        this.gardenRepository = gardenRepository;
    }

    /**
     * Gets all FormResults from persistence
     * @return all FormResults currently saved in persistence
     */
    public List<Garden> getGardens() {
        return gardenRepository.findAll();
    }

    public List<Garden> getOwnedGardens(Long ownerId) {
        return gardenRepository.findByOwnerUserId(ownerId);
    }

    public Garden addGarden(Garden garden) {
        return gardenRepository.save(garden);
    }

    public Optional<Garden> findGarden(Long id) { return gardenRepository.findById(id); }

    public List<Tag> getTags(Long gardenId) {
        Optional<Garden> optionalGarden = gardenRepository.findById(gardenId);
        if (optionalGarden.isPresent()) {
            return optionalGarden.get().getTags();
        } else {
            throw new RuntimeException("Garden not found with id: " + gardenId);
        }
    }

    public Garden addTagToGarden(Long gardenId, Tag tag) {
        Optional<Garden> optionalGarden = gardenRepository.findById(gardenId);
        if (optionalGarden.isPresent()) {
            Garden garden = optionalGarden.get();
            garden.getTags().add(tag);
            return gardenRepository.save(garden);
        } else {
            throw new RuntimeException("Garden not found with id: " + gardenId);
        }
    }
}
