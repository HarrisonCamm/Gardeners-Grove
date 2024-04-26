package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.EditPlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;

import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EditPlantController.class)
public class EditPlantTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlantService plantService;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AuthenticationManager authenticationManager;

    private Garden testGarden;
    private Location testLocation;
    private Plant testPlant;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User("user@email.com", "User", "Name", "password");
        testUser.setUserId(1L);
        Mockito.when(userService.getAuthenicatedUser()).thenReturn(testUser);

        testLocation = new Location("123 Test Street", "Test Suburb", "Test City", "1234", "Test Country");
        testGarden = new Garden("Test Garden", testLocation, "1", testUser);
        testPlant = new Plant(testGarden, "Test Description");
    }

    @Test
    @WithMockUser
    public void RequestPage_NoID_Failure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/edit-plant"))
                .andExpect(status().isBadRequest());
        verify(plantService, never()).findPlant(any(Long.class));
        verify(plantService, never()).getPlants();
    }

    @Test
    @WithMockUser
    public void RequestPage_InvalidID_Failure() throws Exception {
        final Long id = 0L;
        mockMvc.perform(MockMvcRequestBuilders.get("/edit-plant")
                        .param("plantID", id.toString()))
                .andExpect(status().isNotFound());
        verify(plantService).findPlant(id);
    }

    @Test
    @WithMockUser
    public void RequestPage_ValidID_ReturnPage() throws Exception {
        final Long id = 1L;
        Plant plant = new Plant(testGarden, null);
        plant.setId(id);
        when(plantService.findPlant(id)).thenReturn(Optional.of(plant));

        mockMvc.perform(get("/edit-plant")
                        .param("plantID", id.toString()))
                .andExpect(status().isOk());
        verify(plantService).findPlant(id);
    }

    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "1, Carrot, 3453125, 24/1/6353, this is an orange plant",
            "2, oranges, 3453125, 01/10/1234, this is also orange",
            "3, apple, 3453125, 6/12/7554, ''",
            "4, grapefruit, 3453125, 31/7/2024, not a grape"
    })
    public void OnForm_ValidValues_PlantRecordAdded(
            Long plantID, String plantName, String count, String date, String description) throws Exception {
        Plant oldPlant = new Plant(testGarden, "default plant", "1", "a regular plant", "1/1/1111");
        Plant newPlant = new Plant(testGarden, plantName, count, description, date);
        when(plantService.findPlant(plantID)).thenReturn(Optional.of(oldPlant));

        mockMvc.perform(put("/edit-plant")
                        .with(csrf())
                        .param("plantID", plantID.toString())
                        .param("name", plantName)
                        .param("count", count)
                        .param("datePlanted", date)
                        .param("description", description))
                .andExpect(status().is3xxRedirection());

        verify(plantService).findPlant(plantID);
        verify(plantService).addPlant(any(Plant.class));
    }

    @Test
    public void OnForm_InvalidPlantName_ErrorGiven() {

    }

    @Test
    public void OnForm_InvalidDescription_ErrorGiven() {

    }

    @Test
    public void OnForm_InvalidCount_ErrorGiven() {

    }

    @Test
    public void OnForm_DateWrongFormat_ErrorGiven() {

    }

    @Test
    public void OnForm_CancelClicked_ReturnToViewGarden() {

    }
}
