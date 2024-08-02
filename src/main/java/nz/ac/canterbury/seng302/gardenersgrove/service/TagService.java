package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
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

    // Original constructor
    @Autowired
    public TagService(TagRepository tagRepository) {
        this(tagRepository, null, null, null);
    }

    // Secondary constructor without @Autowired
    // Constructor Chaining
    public TagService(TagRepository tagRepository, GardenService gardenService, ModerationService moderationService, UserService userService) {
        this.tagRepository = tagRepository;
        this.gardenService = gardenService;
        this.moderationService = moderationService;
        this.userService = userService;
    }

    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    public Optional<Tag> findTag(Long id) {
        return tagRepository.getTag(id);
    }

    public Tag addTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public void removeTagFromWaitingList(Tag tag) {
        tagRepository.delete(tag);
    }

    @Scheduled(fixedRate = 60000) // Evaluate every 60 seconds
    public void evaluateWaitingTags() {
        // Get all tags that need to be evaluated
        List<Tag> waitingTags = tagRepository.findWaitingTags();

        // Loop through each tag
        for (Tag tag : waitingTags) {
            // Run tag name through moderation service
            if (moderationService.isContentAppropriate(tag.getName())) {
                // Tag is appropriate
                tag.setEvaluated(true);
                tag.setAppropriate(true);

                // Add tag to garden
                gardenService.addTagToGarden(tag.getGardenId(), tag);

                // Remove tag from the waiting list
                removeTagFromWaitingList(tag);
            } else {
                // Tag is not appropriate
                tag.setEvaluated(true);
                tag.setAppropriate(false);


                // Increment users inappropriate tag count
                userService.incrementInappropriateTagCount(tag.getUserID());

                // Remove tag from the waiting list
                removeTagFromWaitingList(tag);
            }
        }
    }
}

