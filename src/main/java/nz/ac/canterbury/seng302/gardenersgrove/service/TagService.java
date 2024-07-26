package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    public List<Tag> getGardenTags(Long gardenId) {
        return tagRepository.findByGardenId(gardenId);
    }

    public Tag addTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public Optional<Tag> findTag(Long id) {
        return tagRepository.getTag(id);
    }

    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    public void deleteAllTags() {
        tagRepository.deleteAll();
    }
}
