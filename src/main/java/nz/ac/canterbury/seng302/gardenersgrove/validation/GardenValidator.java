package nz.ac.canterbury.seng302.gardenersgrove.validation;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import org.springframework.validation.ObjectError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class GardenValidator {

    /**
     * Validates the garden name or garden location
     * @param name garden name
     * @return object error if there is an error otherwise null
     */
    public static ObjectError validateGardenName(String name) {
        //TODO avoid magic numbers
        if (validateWithRegex("^$", name)) { //The garden name is empty
            return new ObjectError("gardenNameError", "Garden name cannot by empty");
        } else if (!validateWithRegex("^[a-zA-Z0-9\\.\\-\\'\\s]*$", name)) {
            return new ObjectError("gardenNameError", "Garden name must only " +
                    "include letters, numbers, spaces, dots, hyphens\n" +
                    "or apostrophes");
        }
        return null;
    }

    /**
     * Validates a garden's location
     * @param location garden location object
     * @return object error if there is an error otherwise null
     */
    public static ObjectError validateGardenLocation(Location location) {
        if (location.getCity().isEmpty() || location.getCountry().isEmpty()) {
            return new ObjectError("gardenLocationError", "City and Country are required");
        }
        return null;
    }

    /**
     * Validates a garden's size
     * @param size garden size
     * @return object error if there is an error otherwise null
     */
    public static ObjectError validateSize(String size) {
        //TODO avoid magic numbers
        if (!size.isEmpty() && !validateWithRegex("[0-9]*[\\.,]?[0-9]*$", size)) {
            return new ObjectError("gardenSizeError", "Garden size must be a positive number");
        }
        return null;
    }

    /**
     * This sees if a regular expression matches with a comparison string
     * @param regex Regular expression
     * @param comparison String that the regular expression will be compared to
     * @return True if it matches otherwise false
     */
    public static boolean validateWithRegex(String regex, String comparison) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(comparison);
        return matcher.matches();
    }



}
