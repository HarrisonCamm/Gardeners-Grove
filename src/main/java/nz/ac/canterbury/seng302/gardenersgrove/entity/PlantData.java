package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantData {

    @JsonDeserialize
    @JsonProperty("id")
    public int id;

    @JsonDeserialize
    @JsonProperty("common_name")
    public String common_name;

    @JsonDeserialize
    @JsonProperty("scientific_name")
    public String scientific_name;

    @JsonDeserialize
    @JsonProperty("image_url")
    public String image_url;

    @JsonDeserialize
    @JsonProperty("family")
    public String family;

    @JsonDeserialize
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

    public String getCommonAndScientificName() {;
        return common_name + ",\n(" + scientific_name + ")";
    }

    public String getScientificName() {
        return scientific_name;
    }

    public String getImageUrl() {
        return image_url;
    }


}

