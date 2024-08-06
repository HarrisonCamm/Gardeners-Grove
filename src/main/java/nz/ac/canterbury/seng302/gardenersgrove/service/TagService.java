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

    //Get all tags from database
    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    //Add tag to database
    public Tag addTag(Tag tag) {
        return tagRepository.save(tag);
    }

    //Get tag by tag id
    public Optional<Tag> findTag(Long id) {
        return tagRepository.getTagById(id);
    }
    //Get tag by tag name
    public Tag getTagByName(String name) {
        return tagRepository.getTagByName(name);
    }
}
