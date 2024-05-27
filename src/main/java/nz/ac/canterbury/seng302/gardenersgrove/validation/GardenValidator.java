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
        if (name.trim().isEmpty()) {
            return new FieldError("garden", "name", "Garden name cannot be empty");
        } else if (name.length() > 255) {
            return new FieldError("garden", "name", "Garden name must be under 255 characters");
        } else if (!validateWithRegex("[0-9\\p{L}\\s'-.]+", name)) {
            return new FieldError("garden", "name", "Garden name must only include letters, numbers, spaces, dots, hyphens, or apostrophes");
        }
        return null;
    }



    /**
     * Validates a garden's location
     * @param location garden location object
     * @return object error if there is an error otherwise null
     */
    public static FieldError validateGardenLocation(Location location, boolean isCity) {
        if (isCity) {
            if (validateWithRegex("^$", location.getCity()) || location.getCity().length() > 255 || location.getCity().trim().isEmpty()) {
                return new FieldError("garden", "location.city", "City name must be under 255 characters and not empty");
            } else if (!validateWithRegex("[\\p{L}\\s'-]+", location.getCity())) {
                return new FieldError("garden", "location.city", "City must only include letters and spaces");
            }
        } else {
            if (validateWithRegex("^$", location.getCountry()) || location.getCountry().length() > 255 || location.getCountry().trim().isEmpty()) {
                return new FieldError("garden", "location.country", "Country name must be under 255 characters and not empty");
            } else if (!validateWithRegex("[\\p{L}\\s'-]+", location.getCountry())) {
                return new FieldError("garden", "location.country", "Country must only include letters and spaces");
            }
        }

        if (location.getSuburb() != null && location.getSuburb().length() > 255) {
            return new FieldError("garden", "location.suburb", "Suburb name must be under 255 characters");
        }


        if (location.getStreetAddress() != null && location.getStreetAddress().length() > 255) {
            return new FieldError("garden", "location.streetAddress", "Street Address name must be under 255 characters");
        }

        return null;
    }


    /**
     * Validates a garden's size
     * @param size garden size
     * @return object error if there is an error otherwise null
     */
    public static FieldError validateSize(String size) {
        if (!size.isEmpty()
                && ((!validateWithRegex("[1-9]+[[\\.,]?([0-9]+)]$", size)
                && !validateWithRegex("[0]*([\\.,]?[0-9]+)+$", size))
                || validateWithRegex("[0]*([\\.,]?[0]+)*$", size))
                || validateWithRegex("^(?=.*[.,].*[.,]).*$", size)) {
            return new FieldError("garden", "size", "Garden size must be a positive number");
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
