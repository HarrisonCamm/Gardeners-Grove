package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
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

    public Garden addGarden(Garden garden) {
        return gardenRepository.save(garden);
    }

    public Optional<Garden> findGarden(Long id) { return gardenRepository.findById(id); }
}
