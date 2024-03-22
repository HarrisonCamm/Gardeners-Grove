package nz.ac.canterbury.seng302.gardenersgrove.integration;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LocationRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;

@SpringBootTest
public class GardenServiceTests {

    private GardenRepository gardenRepository;

    private GardenService gardenService;

    private Garden savedGarden;

    private Location location;

    private Garden returnedGarden;

    @BeforeEach
    public void setUp() {

        gardenRepository = mock(GardenRepository.class);
        gardenService = new GardenService(gardenRepository);

        // Create a location
        location = new Location("test", "test", "test", "test", "test");

        // Create a garden to save
        Garden gardenToSave = new Garden("test", location, "1");

        // Mock the behavior of gardenRepository.save() to return the saved garden
        savedGarden = new Garden("test", location, "1");
        when(gardenRepository.save(any(Garden.class))).thenReturn(savedGarden);

        // Call the addGarden method and store the returned garden
        returnedGarden = gardenService.addGarden(gardenToSave);
    }

    @Test
    public void EmptyRepo_AddGarden_ReturnsGarden() {
        // Assert that the returned garden matches the saved garden
        assertEquals(savedGarden, returnedGarden);
    }

    @Test
    public void NonEmptyRepo_GetGardens_ReturnsGardens() {
        // Mock the behavior of gardenRepository.findAll() to return a list containing the saved garden
        List<Garden> mockedGardens = Collections.singletonList(savedGarden);
        when(gardenRepository.findAll()).thenReturn(mockedGardens);

        // Call the getGardens method and assert that it returns a list with one garden
        assertEquals(1, gardenService.getGardens().size());
    }

    @Test
    public void NonEmptyRepo_GetGardenById_ReturnsCorrectGarden() {
        // Mock the behavior of gardenRepository.findById() to return the saved garden
        when(gardenRepository.findById(any(Long.class))).thenReturn(Optional.of(savedGarden));

        // Call the findGarden method with an arbitrary ID and assert that it returns the saved garden
        assertEquals(Optional.of(savedGarden), gardenService.findGarden(1L));
    }
}


