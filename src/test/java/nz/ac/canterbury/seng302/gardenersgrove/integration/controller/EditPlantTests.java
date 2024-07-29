package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.EditPlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;

import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import nz.ac.canterbury.seng302.gardenersgrove.validation.FieldErrorFactory;
import nz.ac.canterbury.seng302.gardenersgrove.validation.PlantValidator;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.FieldError;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.AdditionalMatchers.not;
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

    @MockBean
    private ImageService imageService;



    private Garden testGarden;
    private Location testLocation;
    private Plant testPlant;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User("user@email.com", "User", "Name", "password");
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

    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "0",
            "-1",
            "-100",
            "' '",
            "''",
            "null"
    })
    public void RequestPage_InvalidID_Failure(String id) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/edit-plant")
                        .param("plantID", id)).andReturn();

        int status = result.getResponse().getStatus();
        assertNotEquals(200, status);
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
            "1, Carrot, 3453125, 24/10/2000, this is an orange plant",
            "2, oranges, '', 01/10/1950, this is also orange",
            "3, grapefruit, 3453125, '', not a grape",
            "4, apple, 3453125, 06/12/2100, ''",
            "5, banana, '', '', peel me",
            "6, dragon fruit, '', 05/04/2050, ''",
            "7, kiwi Fruit, 1, '', ''",
            "8, lemon, '', '', ''",
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

    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "1,  !, 3453125, 24/10/2000, this is an invalid plant",
            "2, \"  \", 3453125, 01/10/1950, this is also invalid",
            "3, \" \", 3453125, 06/12/2100, ''",
            "4, 1123232~#@, 3453125, 31/07/2024, not a grape"
    })
    public void OnForm_InvalidPlantName_ErrorGiven(Long plantID, String plantName, String count, String date, String description) throws Exception {
        Plant oldPlant = new Plant(testGarden, "default plant", "1", "a regular plant", "1/1/1111");
        when(plantService.findPlant(plantID)).thenReturn(Optional.of(oldPlant));

        mockMvc.perform(put("/edit-plant")
                        .with(csrf())
                        .param("plantID", plantID.toString())
                        .param("name", plantName)
                        .param("count", count)
                        .param("datePlanted", date)
                        .param("description", description))
                .andExpect(status().is2xxSuccessful());

        verify(plantService).findPlant(plantID);
    }

    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "1, name, '', '', EQTtqCgSEZTqRynfspZYsrWqbZczPwyWMYERsnmaddRzSOFeprNBJJwkapqpfyxSxKlnbtizTIkzJKQMkvwMryHYKYniFMhiZsnBFjSTZtUAfRnwNNSEIUGCtsZVcRkDpoeynycrYolVgALsPpBotLTmpOYKMGciRqJzoMiXHnRolTEDeijkkNoQgtclZhAYLKWOnOPMKTWYKgwKtEfDmFlHrUYHeHVsKyBLvRGTXLLgwBjmIuKoJHaIDoLShgXuQJHGgfpFlFdiOrBhacpCjApMBJTfMJubUhYXjJIEgWOoZcSJUNRtZwDENcxtYTXLHAFJJAmFpWEVYhWbnzHSKBWoXVfOrtQAcxBMtiQgQKzXfxiHOidoCmhIXmCVqlIjsEIjFfMoiIeycyvJcPlNChLggtebBOqqhRNsEjyqSlSArVWGyNKPUFtWSjSifkrKiFNQZDYOXDWmUcBvnwONbRyHOzgWCkZmVGHrhmomrkoFqfGCakJSkjPIlwjULomDc"
    })
    public void OnForm_InvalidDescription_ErrorGiven(Long plantID, String plantName, String count, String date, String description) throws Exception {
        Plant plant = new Plant(testGarden, "default plant", "1", "a regular plant", "");
        FieldErrorFactory mockFactory = mock(FieldErrorFactory.class);
        PlantValidator.setFieldErrorFactory(mockFactory);
        when(plantService.findPlant(plantID)).thenReturn(Optional.of(plant));
        when(mockFactory.createFieldError(anyString(), anyString(), anyString())).thenCallRealMethod();

        mockMvc.perform(put("/edit-plant")
                        .with(csrf())
                        .param("plantID", plantID.toString())
                        .param("name", plantName)
                        .param("count", count)
                        .param("datePlanted", date)
                        .param("description", description))
                .andExpect(status().isOk());

        verify(mockFactory).createFieldError(eq("plant"), eq("description"), anyString());
    }

    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "1, name, -, '', ''",
            "2, name, -1, '', ''",
            "3, name, -0032425, '', ''",
            "4, name, hi, '', ''",
            "5, name, null, '', ''",
    })
    public void OnForm_InvalidCount_ErrorGiven(Long plantID, String plantName, String count, String date, String description) throws Exception {
        Plant plant = new Plant(testGarden, "default plant", "1", "a regular plant", "");
        FieldErrorFactory mockFactory = mock(FieldErrorFactory.class);
        PlantValidator.setFieldErrorFactory(mockFactory);
        when(plantService.findPlant(plantID)).thenReturn(Optional.of(plant));
        when(mockFactory.createFieldError(anyString(), anyString(), anyString())).thenCallRealMethod();

        mockMvc.perform(put("/edit-plant")
                        .with(csrf())
                        .param("plantID", plantID.toString())
                        .param("name", plantName)
                        .param("count", count)
                        .param("datePlanted", date)
                        .param("description", description))
                .andExpect(status().isOk());

        verify(mockFactory).createFieldError(eq("plant"), eq("count"), anyString());
    }

    @ParameterizedTest
    @WithMockUser
    @CsvSource({
            "1, name, '', 01-01-2024, ''",
            "2, name, '', 2024/01/01, ''",
            "3, name, '', 2024-01-01, ''",
            "4, name, '', 10/05/3000, ''",
            "5, name, '', 10/05/-20000, ''",
    })
    public void OnForm_InvalidDate_ErrorGiven(Long plantID, String plantName, String count, String date, String description) throws Exception {
        Plant plant = new Plant(testGarden, "default plant", "1", "a regular plant", "");
        FieldErrorFactory mockFactory = mock(FieldErrorFactory.class);
        PlantValidator.setFieldErrorFactory(mockFactory);
        when(plantService.findPlant(plantID)).thenReturn(Optional.of(plant));
        when(mockFactory.createFieldError(anyString(), anyString(), anyString())).thenCallRealMethod();

        mockMvc.perform(put("/edit-plant")
                        .with(csrf())
                        .param("plantID", plantID.toString())
                        .param("name", plantName)
                        .param("count", count)
                        .param("datePlanted", date)
                        .param("description", description))
                .andExpect(status().isOk());

        verify(mockFactory).createFieldError(eq("plant"), eq("datePlanted"), anyString());
    }
}
