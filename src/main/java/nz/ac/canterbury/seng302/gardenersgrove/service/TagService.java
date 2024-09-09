package nz.ac.canterbury.seng302.gardenersgrove.service;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    private TagRepository tagRepository;
    private GardenService gardenService; // Add tag to garden
    private ModerationService moderationService; // Perform Moderation
    private UserService userService; // Increment users inappropriate tag count

    /**
     * More primitive constructor for TagService
     * @param tagRepository The tag repository
     */
    public TagService(TagRepository tagRepository) {
        this(tagRepository, null, null, null);
    }

    /**
     * Constructor for TagService
     * @param tagRepository The tag repository
     * @param gardenService The garden service
     * @param moderationService The moderation service
     * @param userService The user service
     */
    @Autowired
    public TagService(TagRepository tagRepository, GardenService gardenService, ModerationService moderationService, UserService userService) {
        this.tagRepository = tagRepository;
        this.gardenService = gardenService;
        this.moderationService = moderationService;
        this.userService = userService;
    }

    //Get all tags from database
    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    public List<Tag> getTagsByEvaluated(boolean evaluated) {
        return tagRepository.findTagsByEvaluated(evaluated);
    }

    public Optional<Tag> findTag(Long id) {
        return tagRepository.getTagById(id);
    }
    //Get tag by tag name
    public Tag getTagByName(String name) {
        return tagRepository.getTagByName(name);
    }

    public Tag addTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public void removeTagFromWaitingList(Tag tag) {
        tagRepository.delete(tag);
    }

    /**
     * Evaluate the next unmoderated tag, called automatically every 5 seconds,
     * the free tier of Azure moderation allows only 1 transaction per second
     */
    @Transactional
    @Scheduled(fixedRate = 5000)
    public void evaluateWaitingTags() {
        // Get the list of all tags that need to be evaluated, check if it's empty first, then get the first tag
        List<Tag> waitingTags = tagRepository.findTagsByEvaluated(false);
        if (waitingTags.isEmpty() || moderationService.isBusy()) {
            return;
        }

        Tag tag = waitingTags.get(0);
        tag.setEvaluated(true);

        // Run tag name through moderation service
        if (moderationService.isContentAppropriate(tag.getName())) {
            addTag(tag);    // Save tag's new details
        } else {
            removeTagFromWaitingList(tag);   // Remove tag from the waiting list

            List<Garden> gardens = gardenService.getGardensByTag(tag);
            for (Garden garden : gardens) {
                // Increment users inappropriate tag count
                userService.incrementInappropriateTagCount(garden.getOwner().getUserId());
                gardenService.removeTagFromGarden(garden.getId(), tag);
            }
        }
    }
}

