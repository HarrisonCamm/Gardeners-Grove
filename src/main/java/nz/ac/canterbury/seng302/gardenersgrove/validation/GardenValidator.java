package nz.ac.canterbury.seng302.gardenersgrove.validation;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class GardenValidator {

    /**
     * Validates the garden name or garden location
     * @param name garden name
     * @return object error if there is an error otherwise null
     */
    public static FieldError validateGardenName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new FieldError("garden", "name", "Garden name cannot be empty");
        } else if (name.length() > 255) {
            return new FieldError("garden", "name", "Garden name must be under 255 characters");
        } else if (!validateWithRegex("[\\p{L}\\d\\s'\\-.,]+", name)) {
            return new FieldError("garden", "name", "Garden name must only include letters, numbers, spaces, dots, hyphens, or apostrophes");
        }
        return null;
    }

    public static FieldError validateGardenDescription(String description) {
        if (!description.isEmpty()) {
            if (description.length() > 512) {
                return new FieldError("garden", "description", "Description must be 512 characters or less and contain some text");
            } else if (!validateWithRegex("^(?=.*[\\p{L}]).+$", description)) {
                return new FieldError("garden", "description", "Description must be 512 characters or less and contain some text");
            }
        }
        return null;
    }

    /**
     * Validates a garden's size
     * @param size garden size
     * @return object error if there is an error otherwise null
     */
    public static FieldError validateSize(String size) {
        double sizeOfEarth = 510100000;

        if (size.length() > 255) {
            return new FieldError("garden", "size", "Garden size must be under 255 characters");
        }

        if (!size.isEmpty()) {
            if ((!validateWithRegex("^[1-9][0-9]*([.,][0-9]+)?$", size)   // Checks for digits 1-9 followed by digits and optional comma/period and digits
                    && !validateWithRegex("^0([.,][0-9]+)?$", size))       // Checks for zero or zero with optional comma/period and digits
                    || validateWithRegex("^(?=.*[.,].*[.,]).*$", size)) { // Checks if there are at least two commas or periods
                return new FieldError("garden", "size", "Garden size must be a positive number");
            }

            if (Double.parseDouble(size.replace(",", ".")) > sizeOfEarth) {
                return new FieldError("garden", "size", "Garden cannot be bigger than the earth");
            }
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
