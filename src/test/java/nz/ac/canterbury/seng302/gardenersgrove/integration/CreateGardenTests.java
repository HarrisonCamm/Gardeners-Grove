package nz.ac.canterbury.seng302.gardenersgrove.integration;

import nz.ac.canterbury.seng302.gardenersgrove.controller.CreateGardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(CreateGardenController.class)
public class CreateGardenTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GardenService gardenService;

    @MockBean
    LocationService locationService;

    @Test
    public void RequestPage_NoFields_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/create-garden"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @ParameterizedTest
    @CsvSource({
            "Lovely Garden, 12.00009123",
            "Tomato's, " +Long.MAX_VALUE,
            "Bob, ''",
    })
    public void PostForm_WithValidFields_Success(String gardenName, String gardenSize) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/create-garden")
                        .param("name", gardenName)
                        .param("location.streetAddress", "test")
                        .param("location.suburb", "test")
                        .param("location.city", "test")
                        .param("location.postcode", "test")
                        .param("location.country", "test")
                        .param("size", gardenSize))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        verify(gardenService).addGarden(any(Garden.class));
    }

    @ParameterizedTest
    @CsvSource({
            "'', 1.49",
            "'',''",
            "Flower_Garden,''",
            "myGarden, -1"
    })
    public void PostForm_WithInvalidFields_ErrorsShown(String gardenName, String gardenSize) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/create-garden")
                        .param("name", gardenName)
                        .param("location.streetAddress", "test")
                        .param("location.suburb", "test")
                        .param("location.city", "test")
                        .param("location.postcode", "test")
                        .param("location.country", "test")
                        .param("size", gardenSize))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(gardenService, times(0)).addGarden(any(Garden.class));
    }

}

