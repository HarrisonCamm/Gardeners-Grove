package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantData;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantGuesserItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantGuesserList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Random;

@Service
public class PlantGuesserService {
    //Retrieved from application-dev.properties
    @Value("${trefle.api.key:#{null}}")
    private String apiKey;

    //Retrieved from application-dev.properties
    @Value("${trefle.api.url:#{null}}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public PlantGuesserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PlantGuesserList getPlantPage(int pageNum) {
        String url = apiUrl + "?page=" + pageNum + "&filter_not[common_name]=null&filter_not[image_url]=null&token=" + apiKey;
        return restTemplate.getForObject(url, PlantGuesserList.class);
    }

    public PlantGuesserItem getPlantById(int id) {
        String url = apiUrl + "/" + id + "?token=" + apiKey;
        return restTemplate.getForObject(url, PlantGuesserItem.class);
    }

    public PlantData[] getPlants() {
        Random random = new Random();
        int num = random.nextInt(747);
        return getPlantPage(num).getPlantGuesserList();
        
    }
    public PlantData getPlant() {
        PlantData[] plantList = getPlants();
        return Arrays.stream(plantList).toList().getFirst();
    }


}
