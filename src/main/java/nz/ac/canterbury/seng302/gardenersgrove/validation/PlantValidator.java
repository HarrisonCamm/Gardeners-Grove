package nz.ac.canterbury.seng302.gardenersgrove.validation;

import org.springframework.validation.ObjectError;

import java.util.regex.Pattern;

public class PlantValidator {

    /**
     * Validates the plant name when creating new Plant objects
     * @param name Plant name
     * @return ObjectError if the plant name is empty or contains invalid characters
     */
    public static ObjectError validatePlantName(String name) {
        if (name.isEmpty()) {
            return new ObjectError("plantName", "Plant name cannot be empty");
        } else if (!isValidPlantName(name)) {
            return new ObjectError("plantName", "Plant name must only include letters, numbers, spaces, dots, hyphens, or apostrophes");
        }
        return null;
    }

    /**
     * Validates the plant count when creating new Plant objects
     * @param count Plant count
     * @return ObjectError if the count is not a positive number
     */
    public static ObjectError validatePlantCount(String count) {
        if (!isPositiveNumber(count)) {
            return new ObjectError("plantCount", "Plant count must be a positive number");
        }
        return null;
    }

    /**
     * Validates the plant description when creating new Plant objects
     * @param description Plant description
     * @return ObjectError if the description is longer than 512 characters
     */
    public static ObjectError validatePlantDescription(String description) {
        if (description.length() > 512) {
            return new ObjectError("plantDescription", "Plant description must be less than 512 characters");
        }
        return null;
    }

    /**
     * Validates if the input is a positive number
     * @param input Input string to validate
     * @return True if the input is a positive number, otherwise false
     */
    private static boolean isPositiveNumber(String input) {
        // Regex to match a string that contains only numerical digits,
        String regex = "^[0-9]*$";
        return Pattern.matches(regex, input);
    }

    /**
     * Validates the plant name
     * @param name Plant name
     * @return True if the plant name is valid, otherwise false
     */
    private static boolean isValidPlantName(String name) {
        // Regular expression to match alphanumeric characters, spaces, dots, commas, hyphens, and apostrophes
        String regex = "^[a-zA-Z0-9\\s.,'-]+$";
        return Pattern.matches(regex, name);
    }
}
