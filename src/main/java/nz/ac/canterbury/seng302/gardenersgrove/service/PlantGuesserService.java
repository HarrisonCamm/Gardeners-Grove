package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantData;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantGuesserItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantGuesserList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.min;

@Service
public class PlantGuesserService {

    Logger logger = LoggerFactory.getLogger(PlantGuesserService.class);
    //Retrieved from application-dev.properties
    @Value("${trefle.api.key:#{null}}")
    private String apiKey;

    //Retrieved from application-dev.properties
    @Value("${trefle.api.url:#{null}}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private Random random = new Random();
    private static final String PLANT_PAGE_FILTERS = "&filter_not[common_name]=null&filter_not[image_url]=null&token=";
    private static final String PLANT_FAMILY_PAGE_FILTERS = "&filter_not[common_name]=null&token=";
    private static final int MAX_PAGE_NUM = 740;
    private static final int NUM_FAMILY_OPTIONS = 3;

    @Autowired
    public PlantGuesserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PlantGuesserList getPlantPage(int pageNum) {
        String url = apiUrl + "?page=" + pageNum + PLANT_PAGE_FILTERS + apiKey;
        try {
            return restTemplate.getForObject(url, PlantGuesserList.class);
        } catch (Exception e) {
            logger.info("Invalid Trefle URL");
            return null; // Return null for invalid or error responses e.g. no token
        }
    }
    public PlantGuesserList getPlantFamily(String family) {
        String url = apiUrl + "?filter[family_name]=" + family + PLANT_FAMILY_PAGE_FILTERS + apiKey;
        return restTemplate.getForObject(url, PlantGuesserList.class);
    }

    public PlantGuesserItem getPlantById(int id) {
        String url = apiUrl + "/" + id + "?token=" + apiKey;
        try {
            return restTemplate.getForObject(url, PlantGuesserItem.class);
        } catch (Exception e) {
            return null; //e.g. invalid ID
        }
    }

    public PlantData[] getPlants() {
        int num = random.nextInt(MAX_PAGE_NUM);
        return getPlantPage(num).getPlantGuesserList();
        
    }
    public PlantData getPlant(int i) {
        List<PlantData> plantList= new ArrayList<>(Arrays.stream(getPlants()).toList());
        Collections.shuffle(plantList);
        return plantList.get(i);
    }

    public PlantData[] getFamilyPlants(String family, String plantName) {
        PlantData[] plantList = getPlantFamily(family).getPlantGuesserList();
        return Arrays.stream(plantList).toList()
                .stream()
                .filter(plant -> !Objects.equals(plant.common_name, plantName))
                .toArray(PlantData[]::new);
    }

    public List<String> getMultichoicePlantNames(String family, String plantName, String commonAndScientificName) {
        PlantData[] plantList = getFamilyPlants(family, plantName);
        List<String> multichoicePlantNames = new ArrayList<>(Arrays.stream(plantList).toList()
                .stream()
                .map(PlantData::getCommonAndScientificName)
                .toList());
        Collections.shuffle(multichoicePlantNames);
        List<String> plant = Collections.singletonList(commonAndScientificName);
        return Stream.concat(multichoicePlantNames.subList(0, min(NUM_FAMILY_OPTIONS, multichoicePlantNames.size())).stream(), plant.stream())
                    .collect(Collectors.toList());


    }

}
