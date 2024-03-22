package nz.ac.canterbury.seng302.gardenersgrove.integration;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.AutocompleteService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest
public class CreatePlantTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlantService plantService;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private AutocompleteService autocompleteService;

    @MockBean
    private LocationService locationService;

    private Garden testGarden;
    private Location testLocation;
    private Plant testPlant;

    @BeforeEach
    public void setUp() {
        testLocation = new Location("123 Test Street", "Test Suburb", "Test City", "1234", "Test Country");
        testGarden = new Garden("Test Garden", testLocation, "1");
        testPlant = new Plant(testGarden, "Test Description");
    }

    @Test
    public void GetPage_NoFields_Success() throws Exception {

        when(plantService.findPlant(any(Long.class))).thenReturn(Optional.ofNullable(testPlant));
        when(gardenService.findGarden(any(Long.class))).thenReturn(Optional.ofNullable(testGarden));

        mockMvc.perform(MockMvcRequestBuilders.get("/create-plant")
                .queryParam("gardenID", String.valueOf(1L))) // Any number will do
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void GetPage_NoFields_Failure() throws Exception {

        when(plantService.findPlant(any(Long.class))).thenReturn(Optional.ofNullable(testPlant));
        when(gardenService.findGarden(any(Long.class))).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(MockMvcRequestBuilders.get("/create-plant")
                .queryParam("gardenID", String.valueOf(1L))) // Any number will do
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @ParameterizedTest
    @CsvSource({
            "Tomato, " + Integer.MAX_VALUE + ", Yummy Tomato",
            "Potato, 2, Test Description",
    })
    public void PostForm_WithFields_Redirects(String name, String count, String description) throws Exception {
        when(plantService.findPlant(any(Long.class))).thenReturn(Optional.ofNullable(testPlant));
        when(gardenService.findGarden(any(Long.class))).thenReturn(Optional.ofNullable(testGarden));

        mockMvc.perform(MockMvcRequestBuilders.post("/create-plant")
                .queryParam("gardenID", String.valueOf(1L))
                .queryParam("plantDatePlanted", "2021-01-01")
                .param("name", name)
                .param("count", count)
                .param("description", description))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        verify(plantService).addPlant(any(Plant.class));
    }

    @ParameterizedTest
    @CsvSource({
            "Tomato, " + Integer.MIN_VALUE + ", Yummy Tomato",
            "Potato____, 0, Test Description",
            "Potato, -1, Test Description",
    })
    public void PostForm_WithInvalidFields_ErrorsShown(String name, String count, String description) throws Exception {
        when(plantService.findPlant(any(Long.class))).thenReturn(Optional.ofNullable(testPlant));
        when(gardenService.findGarden(any(Long.class))).thenReturn(Optional.ofNullable(testGarden));

        mockMvc.perform(MockMvcRequestBuilders.post("/create-plant")
                .queryParam("gardenID", String.valueOf(1L))
                .queryParam("plantDatePlanted", "2021-01-01")
                .param("name", name)
                .param("count", count)
                .param("description", description))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(plantService, Mockito.times(0)).addPlant(any(Plant.class));
    }
}
