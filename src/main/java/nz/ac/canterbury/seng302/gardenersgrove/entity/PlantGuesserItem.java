package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantGuesserItem {

    @JsonDeserialize
    @JsonProperty("data")
    private PlantData plantData;


    public int getId() {
        return plantData.id;
    }

    public String getCommonName() {
        return plantData.common_name;
    }

    public String getScientificName() {
        return plantData.scientific_name;
    }

    public String getImageUrl() {
        return plantData.image_url;
    }

}
