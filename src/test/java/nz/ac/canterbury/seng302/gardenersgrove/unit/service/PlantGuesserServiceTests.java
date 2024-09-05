package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantData;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantGuesserItem;
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
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;


public class PlantGuesserServiceTests {
    private PlantGuesserService plantGuesserService;
    private static final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

    private static String plantFamilyResponseJsonString;
    private static String plantsResponseJsonString;
    private static String invalidTokenPlantsResponseJsonString;
    private static String invalidPlantIdResponseJsonString;


    private static PlantGuesserList plantGuesserList;
    private static PlantGuesserList plantGuesserFamilyList;




    @BeforeAll
    static public void  jsonSetup() throws Exception {
        //read json example file
        plantFamilyResponseJsonString = Files.readString(Paths.get("src/test/resources/json/getPlantFamilyResponse.json"));
        plantsResponseJsonString = Files.readString(Paths.get("src/test/resources/json/getPlantsResponse.json"));
        invalidTokenPlantsResponseJsonString = Files.readString(Paths.get("src/test/resources/json/getPlantsNoTokenResponse.json"));
        invalidPlantIdResponseJsonString = Files.readString(Paths.get("src/test/resources/json/getPlantInvalidIdResponse.json"));


        ObjectMapper objectMapper = new ObjectMapper();
        plantGuesserList = objectMapper.readValue(plantsResponseJsonString, PlantGuesserList.class);

        plantGuesserFamilyList = objectMapper.readValue(plantFamilyResponseJsonString, PlantGuesserList.class);


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

    @Test
    public void validFamilyRequest_ReturnsPlantGuesserList() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(plantGuesserFamilyList);
        PlantGuesserList plantGuesserFamilyList = plantGuesserService.getPlantFamily("Pinaceae"); //same family as mocked json
        Assertions.assertNotNull(plantGuesserFamilyList);

        PlantData[] plantGuesserItems = plantGuesserFamilyList.getPlantGuesserList();
        String plantItemCommonName = Arrays.stream(plantGuesserItems).toList().get(0).common_name;
        Assertions.assertEquals("Scotch pine", plantItemCommonName);

    }

    @Test
    public void invalidPlantId_ReturnsNull() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(invalidPlantIdResponseJsonString);
        int id = 0;
        PlantGuesserItem response = plantGuesserService.getPlantById(id);
        Assertions.assertNull(response);
    }

    @Test
    public void getPlant_ReturnsValidPlant() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(plantGuesserList);

        PlantData response = plantGuesserService.getPlant();

        Assertions.assertTrue(plantsResponseJsonString.contains(response.scientific_name));

    }

    @Test
    public void getFamilyPlants_ExcludesPlantOfSameName() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(plantGuesserFamilyList);
        String familyString = "Pinaceae";
        String correctPlantName = "Mountain pine";

        PlantData[] response = plantGuesserService.getFamilyPlants(familyString, correctPlantName);
        boolean containsCorrectName = Arrays.stream(response).anyMatch(plantData -> Objects.equals(plantData.common_name, correctPlantName));
        Assertions.assertFalse(containsCorrectName);
    }

    @Test
    public void getFamilyPlants_OnlyOnePlant_ReturnsNull() {
        //TODO
    }

    @Test
    public void getMultiChoice_ContainsCorrectAnswer() {
        //TODO
    }

    @Test
    public void getMultiChoice_LessThanThreeOptions_ReturnsNull() {
        //TODO
    }
}
