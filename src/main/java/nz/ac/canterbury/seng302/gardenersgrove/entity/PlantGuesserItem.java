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
    @JsonDeserialize
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlantData {

        @JsonProperty("id")
        private int id;

        @JsonProperty("common_name")
        private String common_name;

        @JsonProperty("scientific_name")
        private String scientific_name;

        @JsonProperty("image_url")
        private String image_url;

        public PlantData() {
            // no-args jackson constructor
        }
    }
}
