package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TagServiceTests {

    private static TagService tagService;

    private static GardenService gardenService;

    @Mock
    private static TagRepository tagRepository;

    @Mock
    private static GardenRepository gardenRepository;

    @BeforeAll
    public static void setUp() {
        tagRepository = mock(TagRepository.class);
        gardenRepository = mock(GardenRepository.class);
        tagService = new TagService(tagRepository);
        gardenService = new GardenService(gardenRepository);

        Garden testGarden = new Garden(
                "Garden",
                new Location("test", "test", "test", "test", "test"),
                "1");

        Tag tag1 = new Tag("tag1", true);
        Tag tag2 = new Tag("tag2", true);

        when(tagRepository.findAll()).thenReturn(Arrays.asList(tag1, tag2)); // Mock the findAll method
        when(tagRepository.save(tag1)).thenReturn(tag1);
        when(tagRepository.save(tag2)).thenReturn(tag2);
        when(tagRepository.getTagById(1L)).thenReturn(Optional.of(tag1));
        when(tagRepository.getTagById(2L)).thenReturn(Optional.of(tag2));


        when(gardenRepository.findById(1L)).thenReturn(Optional.of(testGarden));
        when(gardenRepository.save(testGarden)).thenReturn(testGarden);

        gardenService.addGarden(testGarden);

        tagService.addTag(tag1);
        tagService.addTag(tag2);
    }

    @Test
    public void testGetTags() {
        assertEquals(2, tagService.getTags().size());
    }

    @Test
    public void testFindTag() {
        assertTrue(tagService.findTag(1L).isPresent());
        assertTrue(tagService.findTag(2L).isPresent());
    }
}
