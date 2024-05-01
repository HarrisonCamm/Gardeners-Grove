package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Plants, defined by the @link{Service} annotation.
 * This class links automatically with @link{PlantRepository} and @link{GardenRepository}, see the @link{Autowired} annotation below
 */
@Service
public class PlantService {
    private final PlantRepository plantRepository;

    @Autowired
    public PlantService(PlantRepository plantRepository, GardenRepository gardenRepository) {
        this.plantRepository = plantRepository;
    }

    public List<Plant> getPlants() { return plantRepository.findAll(); }

    public List<Plant> getGardenPlant(Long gardenId) { return plantRepository.findByGardenId(gardenId); }

    public Plant addPlant(Plant plant) { return plantRepository.save(plant); }

    public Optional<Plant> findPlant(Long id) { return plantRepository.findById(id); }

}
