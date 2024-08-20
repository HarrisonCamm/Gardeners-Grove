package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantData {

    @JsonProperty("id")
    public int id;

    @JsonProperty("common_name")
    public String common_name;

    @JsonProperty("scientific_name")
    public String scientific_name;

    @JsonProperty("image_url")
    public String image_url;

    @JsonProperty("family")
    public String family;

    @JsonProperty("family_common_name")
    public String family_common_name;

    public PlantData() {
        // no-args jackson constructor
    }

    public int getId() {
        return id;
    }

    public String getCommonName() {
        return common_name;
    }

    public String getScientificName() {
        return scientific_name;
    }

    public String getImageUrl() {
        return image_url;
    }


}

