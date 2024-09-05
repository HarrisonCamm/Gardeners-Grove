package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantData;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantGuesserItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantGuesserList;
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
    //Retrieved from application-dev.properties
    @Value("${trefle.api.key:#{null}}")
    private String apiKey;

    //Retrieved from application-dev.properties
    @Value("${trefle.api.url:#{null}}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private Random random = new Random();

    @Autowired
    public PlantGuesserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PlantGuesserList getPlantPage(int pageNum) {
        String url = apiUrl + "?page=" + pageNum + "&filter_not[common_name]=null&filter_not[image_url]=null&token=" + apiKey;
        try {
            return restTemplate.getForObject(url, PlantGuesserList.class);
        } catch (Exception e) {
            return null; // Return null for invalid or error responses e.g. no token
        }
    }
    public PlantGuesserList getPlantFamily(String family) {
        String url = apiUrl + "?filter[family_name]=" + family + "&filter_not[common_name]=null&token=" + apiKey;
        return restTemplate.getForObject(url, PlantGuesserList.class);
    }

    public PlantGuesserItem getPlantById(int id) {
        String url = apiUrl + "/" + id + "?token=" + apiKey;
        return restTemplate.getForObject(url, PlantGuesserItem.class);
    }

    public PlantData[] getPlants() {
        int num = random.nextInt(747);
        return getPlantPage(num).getPlantGuesserList();
        
    }
    public PlantData getPlant() {
        List<PlantData> plantList= new ArrayList<>(Arrays.stream(getPlants()).toList());
        Collections.shuffle(plantList);
        return plantList.get(0);
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
        return Stream.concat(multichoicePlantNames.subList(0,min(3, multichoicePlantNames.size())).stream(), plant.stream())
                .collect(Collectors.toList());
    }

}
