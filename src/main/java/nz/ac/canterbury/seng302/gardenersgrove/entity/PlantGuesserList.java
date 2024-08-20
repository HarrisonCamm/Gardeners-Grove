package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Arrays;

public class PlantGuesserList {

    @JsonDeserialize
    @JsonProperty("data")
    private PlantData[] plantGuesserItems;

    public PlantGuesserList() {
        // no-args jackson constructor
    }
    public PlantData[] getPlantGuesserList() {
        return plantGuesserItems;
    }

}
