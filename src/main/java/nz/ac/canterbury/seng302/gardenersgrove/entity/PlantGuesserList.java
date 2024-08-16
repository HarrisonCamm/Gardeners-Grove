package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class PlantGuesserList {

    @JsonDeserialize
    @JsonProperty("list")
    private PlantGuesserItem[] plantGuesserItems;
}
