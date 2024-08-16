package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Arrays;

public class PlantGuesserList {

    @JsonDeserialize
    @JsonProperty("list")
    private PlantGuesserItem[] plantGuesserItems;

    public PlantGuesserList() {
        // no-args jackson constructor
    }

    public PlantGuesserItem[] getPlantGuesserList() {
        return plantGuesserItems;
    }

    public PlantGuesserItem getPlant() {
        PlantGuesserItem[] plantList = getPlantGuesserList();
        return Arrays.stream(plantList).toList().getFirst();
    }
}
