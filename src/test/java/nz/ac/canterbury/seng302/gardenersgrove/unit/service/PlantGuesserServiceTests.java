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
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;


class PlantGuesserServiceTests {
    private PlantGuesserService plantGuesserService;
    private static final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

    private static String plantFamilyResponseJsonString;
    private static String plantsResponseJsonString;
    private static String invalidTokenPlantsResponseJsonString;
    private static String invalidPlantIdResponseJsonString;
    private static PlantGuesserList plantGuesserList;
    private static PlantGuesserList plantGuesserFamilyList;





    @BeforeAll
    static void  jsonSetup() throws Exception {
        //read json example file
        plantFamilyResponseJsonString = Files.readString(Paths.get("src/test/resources/json/getSinglePlantFamilyResponse.json"));
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
    void validApiRequest_ReturnsPlantGuesserList() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(plantGuesserList);

        int pageNum = 1;
        PlantGuesserList tempPlantGuesserList = plantGuesserService.getPlantPage(pageNum);
        Assertions.assertNotNull(tempPlantGuesserList);

        PlantData[] plantGuesserItems = tempPlantGuesserList.getPlantGuesserList();
        String plantItemCommonName = Arrays.stream(plantGuesserItems).toList().get(0).common_name;
        Assertions.assertEquals("Benguet pine", plantItemCommonName);
    }

    @Test
    void invalidTokenRequest_ReturnsNull() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(invalidTokenPlantsResponseJsonString);
        int pageNum = 1;
        PlantGuesserList response = plantGuesserService.getPlantPage(pageNum);
        Assertions.assertNull(response);
    }

    @Test
    void validFamilyRequest_ReturnsPlantGuesserList() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(plantGuesserFamilyList);
        PlantGuesserList tempPlantGuesserFamilyList = plantGuesserService.getPlantFamily("Pinaceae"); //same family as mocked json
        Assertions.assertNotNull(tempPlantGuesserFamilyList);

        PlantData[] plantGuesserItems = tempPlantGuesserFamilyList.getPlantGuesserList();
        String plantItemCommonName = Arrays.stream(plantGuesserItems).toList().get(0).common_name;
        Assertions.assertEquals("Scotch pine", plantItemCommonName);

    }

    @Test
    void invalidPlantId_ReturnsNull() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(invalidPlantIdResponseJsonString);
        int id = 0;
        PlantGuesserItem response = plantGuesserService.getPlantById(id);
        Assertions.assertNull(response);
    }

    @Test
    void getPlant_ReturnsValidPlant() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(plantGuesserList);

        PlantData response = plantGuesserService.getPlant(0);

        Assertions.assertTrue(plantsResponseJsonString.contains(response.common_name));

    }

    @Test
    void getPlantRound_ReturnsValidPlants() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(plantGuesserList);

        List<PlantData> response = plantGuesserService.getPlantRound();

        for (PlantData plant: response) {
            Assertions.assertTrue(plantsResponseJsonString.contains(plant.common_name));
        }

    }
    @Test
    void getFamilyPlants_ExcludesPlantOfSameName() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(plantGuesserFamilyList);
        String familyString = "Pinaceae";
        String correctPlantName = "Mountain pine";

        PlantData[] response = plantGuesserService.getFamilyPlants(familyString, correctPlantName);
        boolean containsCorrectName = Arrays.stream(response).anyMatch(plantData -> Objects.equals(plantData.common_name, correctPlantName));
        Assertions.assertFalse(containsCorrectName);
    }

    @Test
    void getMultiChoice_DoesNotContainCorrectAnswer() {
        Mockito.when(restTemplate.getForObject(any(String.class), any())).thenReturn(plantGuesserFamilyList);
        String familyString = "Pinaceae";
        String correctPlantName = "Mountain pine";
        String plantScientificName = "Pinus uncinata";
        String commonAndScientificName = correctPlantName + ",\n(" + plantScientificName + ")";

        List<String> response = plantGuesserService.getMultichoicePlantNames(familyString, correctPlantName, commonAndScientificName);
        boolean containsCorrectName = response.stream().anyMatch(plantData -> Objects.equals(plantData, correctPlantName));
        Assertions.assertFalse(containsCorrectName);
    }

    @Test
    void excludePlantFamilies_IsOkay() {
        try
        {
            plantGuesserService.excludePlantFamilies();
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

}
