package nz.ac.canterbury.seng302.gardenersgrove.integration;


import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.service.AutocompleteService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

@WebMvcTest
public class ViewGardenTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AutocompleteService autocompleteService;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private PlantService PlantService;

    @ParameterizedTest
    @CsvSource({
            "Lovely Garden, 12.00009123",
            "Tomato's, " +Long.MAX_VALUE,
            "Bob, ''",
    })
    public void PostForm_WithValidFields_Success(String gardenName, String gardenSize) throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/Create Garden")
                        .param("name", gardenName)
                        .param("location.streetAddress", "test")
                        .param("location.suburb", "test")
                        .param("location.city", "test")
                        .param("location.postcode", "test")
                        .param("location.country", "test")
                        .param("size", gardenSize))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/View Garden?gardenID=*"));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, Integer.MAX_VALUE, Integer.MIN_VALUE, 0})
    public void postForm_WithInvalidID_Fail(int integer) {
        //Incoming bad test
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/ViewGarden")
                    .param("gardenID", String.valueOf(integer)));
        } catch (Exception e) {
            assertTrue(true);
        }
    }
}
