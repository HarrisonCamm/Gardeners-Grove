package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantData;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantGuesserList;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantGuesserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;


public class PlantGuesserServiceTests {
    private PlantGuesserService plantGuesserService;
    private static final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

    private static String plantFamilyResponseJsonString;
    private static String plantsResponseJsonString;
    private static String invalidTokenPlantsResponseJsonString;

    private static PlantGuesserList plantGuesserList;



    @BeforeAll
    static public void  jsonSetup() throws Exception {
        //read json example file
        plantFamilyResponseJsonString = Files.readString(Paths.get("src/test/resources/json/getPlantFamilyResponse.json"));
        plantsResponseJsonString = Files.readString(Paths.get("src/test/resources/json/getPlantsResponse.json"));
        invalidTokenPlantsResponseJsonString = Files.readString(Paths.get("src/test/resources/json/getPlantsNoTokenResponse.json"));


        ObjectMapper objectMapper = new ObjectMapper();
        plantGuesserList = objectMapper.readValue(plantsResponseJsonString, PlantGuesserList.class);

    }

    @BeforeEach
    public void setUp() {
        //mock any classes here if needed
        plantGuesserService = new PlantGuesserService(restTemplate);
    }

    @Test
    public void validApiRequest_ReturnsPlantGuesserList() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(plantGuesserList);

        int pageNum = 1;
        PlantGuesserList plantGuesserList = plantGuesserService.getPlantPage(pageNum);
        Assertions.assertNotNull(plantGuesserList);

        PlantData[] plantGuesserItems = plantGuesserList.getPlantGuesserList();
        String plantItemCommonName = Arrays.stream(plantGuesserItems).toList().get(0).common_name;
        Assertions.assertEquals("Benguet pine", plantItemCommonName);
    }

    @Test
    public void invalidTokenRequest_ReturnsNull() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(invalidTokenPlantsResponseJsonString);
        int pageNum = 1;
        PlantGuesserList response = plantGuesserService.getPlantPage(pageNum);
        Assertions.assertNull(response);
    }
}
