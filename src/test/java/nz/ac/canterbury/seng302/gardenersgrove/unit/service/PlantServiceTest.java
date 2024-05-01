package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class PlantServiceTest {

    @Autowired
    private PlantRepository plantRepository;
    private PlantService plantService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GardenRepository gardenRepository;

    @MockBean
    private UserService userService;

    private Garden mockGarden;
    private User mockOwner;

    @BeforeEach
    public void setup() {
        plantService = new PlantService(plantRepository, gardenRepository);
        mockOwner = new User("user@email.com", "User", "Name", "password");
        userRepository.save(mockOwner);
        mockGarden = new Garden("Test Garden", null, null, mockOwner);
        gardenRepository.save(mockGarden);
    }

    @Test
    public void addPlantTest() {
        Plant plant = new Plant(mockGarden, "carrot");
        plantService.addPlant(plant);
        assertTrue(plantRepository.findAll().contains(plant));
    }

    @Test
    public void getPlantsTest() {
        Plant plant1 = new Plant(mockGarden, "carrot");
        Plant plant2 = new Plant(mockGarden, "apple");
        Plant plant3 = new Plant(mockGarden, "orange");
        plantService.addPlant(plant1);
        plantService.addPlant(plant2);
        plantService.addPlant(plant3);

        List<Plant> plants = plantService.getPlants();
        assertTrue(plants.contains(plant1) && plants.contains(plant2) && plants.contains(plant3));
    }

    @Test
    public void findPlantTest() {
        Plant plant = new Plant(mockGarden, "carrot");
        plantService.addPlant(plant);
        Optional<Plant> foundPlant = plantService.findPlant(plant.getId());
        assertTrue(foundPlant.isPresent() && foundPlant.get().equals(plant));
    }

    @Test
    public void getGardenPlantTest() {
        Plant plant1 = new Plant(mockGarden, "carrot");
        Plant plant2 = new Plant(mockGarden, "apple");
        Plant plant3 = new Plant(mockGarden, "orange");
        plantService.addPlant(plant1);
        plantService.addPlant(plant2);
        plantService.addPlant(plant3);

        List<Plant> plants = plantService.getGardenPlant(mockGarden.getId());
        assertTrue(plants.contains(plant1) && plants.contains(plant2) && plants.contains(plant3));
        assertArrayEquals(List.of(plant1, plant2, plant3).toArray(), plants.toArray());
    }

}
