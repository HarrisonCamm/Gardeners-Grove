package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for FormResults, defined by the @link{Service} annotation.
 * This class links automatically with @link{FormRepository}, see the @link{Autowired} annotation below
 */
@Service
public class GardenService {
    private static final int GARDENS_PER_PAGE = 10;
    private GardenRepository gardenRepository;

    @Autowired
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

    public List<Garden> getGardensByTag(Tag tag) {
        return gardenRepository.findGardensByTagId(tag.getId());
    }

    public Page<Garden> getPublicGardens(Integer page) {
        return gardenRepository.findPublicGardens(PageRequest.of(page, GARDENS_PER_PAGE));
    }

    public Page<Garden> searchPublicGardens(String search, Integer page) {
        if (search == null || search.isEmpty()) {
            return getPublicGardens(page);
        }
        return gardenRepository.findPublicGardensBySearch(search, PageRequest.of(page, GARDENS_PER_PAGE));
    }

    public Garden addGarden(Garden garden) {
        return gardenRepository.save(garden);
    }
    public Garden updateGarden(Garden garden, String name, Location location, String size, Boolean isPublic, String description) {
        garden.setName(name);
        garden.setLocation(location);
        garden.setSize(size);
        garden.setIsPublic(isPublic);
        garden.setDescription(description);
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

    public List<Tag> getEvaluatedTags(Long gardenId) {
        List<Tag> tags = getTags(gardenId);
        tags.removeIf(tag -> !tag.getEvaluated());
        return tags;
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

    public Garden removeTagFromGarden(Long gardenId, Tag tag) {
        Optional<Garden> optionalGarden = gardenRepository.findById(gardenId);
        if (optionalGarden.isPresent()) {
            Garden garden = optionalGarden.get();
            garden.getTags().remove(tag);
            return gardenRepository.save(garden);
        } else {
            throw new RuntimeException("Garden not found with id: " + gardenId);
        }
    }

    public Page<Garden> searchPublicGardensByTags(List<Long> tagIds, Integer page) {
        return gardenRepository.findPublicGardensByTags(tagIds, PageRequest.of(page, GARDENS_PER_PAGE));
    }

    public Page<Garden> searchPublicGardensBySearchAndTags(String search, Integer page, List<Long> tagIds) {
        return gardenRepository.findPublicGardensBySearchAndTags(search, PageRequest.of(page, GARDENS_PER_PAGE), tagIds);
    }

    /**
     * Updates and saves a garden with a new unclaimed bloom tips amount
     * @param gardenID the garden to update
     * @param tipAmount the tip amount
     */
    public void addUnclaimedBloomTips(Long gardenID, Integer tipAmount) {
        Garden garden = gardenRepository.findById(gardenID).get();

        // Add to the total number of blooms tipped
        garden.setTotalBloomTips(garden.getTotalBloomTips() + tipAmount);

        // Set the new amount of unclaimed tips by adding the old amount to the new amount
        garden.setUnclaimedBlooms(garden.getUnclaimedBlooms() + tipAmount);
        gardenRepository.save(garden);
    }

    public void removeUnclaimedBloomTips(Garden garden) {
        garden.setUnclaimedBlooms(0);
        gardenRepository.save(garden);
    }
}
