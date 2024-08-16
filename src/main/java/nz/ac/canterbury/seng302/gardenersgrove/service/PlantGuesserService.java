package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantGuesserItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantGuesserList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public PlantGuesserList getPlantsForGame() {
        String url = apiUrl + "?token=" + apiKey;
        return restTemplate.getForObject(url, PlantGuesserList.class);
    }

    public PlantGuesserItem getPlantById(int id) {
        String url = apiUrl + "/" + id + "?token=" + apiKey;
        return restTemplate.getForObject(url, PlantGuesserItem.class);
    }


}
