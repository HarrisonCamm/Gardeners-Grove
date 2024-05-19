package nz.ac.canterbury.seng302.gardenersgrove.validation;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LocationValidator {
    /**
     * Validates the street address input.
     * Ensures that the street address contains only letters, numbers, spaces, commas, periods, hyphens, or apostrophes.
     * This is to prevent invalid characters from being entered and to maintain a standard format for addresses.
     *
     * @param address The street address input from the user.
     * @return FieldError if the validation fails, otherwise null if the input is valid.
     */
    public static FieldError validateStreetAddress(String address) {
        if (!validateWithRegex("^[\\p{L}0-9\\s'-.,]+$", address)) {
            return new FieldError("garden", "location.streetAddress", "Street address must only include letters, numbers, spaces, commas, periods, hyphens, or apostrophes");
        }
        return null;
    }

    /**
     * Validates the suburb input.
     * Allows only letters, spaces, hyphens, and apostrophes to maintain a standard and accurate addressing format.
     * This ensures the suburb name adheres to common naming conventions without unusual characters.
     *
     * @param suburb The suburb input from the user.
     * @return FieldError if the validation fails, otherwise null if the input is valid.
     */
    public static FieldError validateSuburb(String suburb) {
        if (!validateWithRegex("^[\\p{L}\\s'-]*$", suburb)) {
            return new FieldError("garden", "location.suburb", "Suburb must only include letters, spaces, hyphens, or apostrophes");
        }
        return null;
    }

    /**
     * Validates the postcode input.
     * Ensures the postcode consists of exactly 5 digits, which is a common format for postcodes.
     * This validation is important to ensure that the postcode adheres to expected numeric formats which are used for sorting and delivery purposes.
     * Note: Adjust the regex as needed based on local postcode rules.
     *
     * @param postcode The postcode input from the user.
     * @return FieldError if the validation fails, otherwise null if the input is valid.
     */
    public static FieldError validatePostcode(String postcode) {
        if (!validateWithRegex("^[0-9]{5}$", postcode)) { // Assuming postcode is 5 digits. Adjust regex as needed.
            return new FieldError("garden", "location.postcode", "Postcode must be a 5-digit number");
        }
        return null;
    }



    public static FieldError validateCity(String city) {
        if (validateWithRegex("^$", city)) {
            return new FieldError("garden", "location.city", "City is required");
        } else if (!validateWithRegex("[\\p{L}\\s'-]+", city)) {
            return new FieldError("garden", "location.city", "City must only include letters and spaces");
        }
        return null;
    }

    public static FieldError validateCountry(String country) {
        if (validateWithRegex("^$", country)) {
            return new FieldError("garden", "location.country", "Country is required");
        } else if (!validateWithRegex("[\\p{L}\\s'-]+", country)) {
            return new FieldError("garden", "location.country", "Country must only include letters and spaces");
        }
        return null;
    }





    //TODO: Abstract validateWithRegex out so its reused by both GardenValidator and PlantValidator

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
