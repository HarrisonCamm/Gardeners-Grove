package nz.ac.canterbury.seng302.gardenersgrove.validation;

import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class PlantValidator {

    /**
     * Validates the plant name when creating new Plant objects
     * @param name Plant name
     * @return FieldError if the plant name is empty or contains invalid characters
     */
    public static FieldError validatePlantName(String name) {
        if (name == null || name.isEmpty() || !isValidPlantName(name)) {
            return new FieldError("plant", "name",
                    "Plant name cannot be empty and must only include letters, numbers, spaces, dots, " +
                            "hyphens or apostrophes.");
        }

        if (name.length() > 255){
            return new FieldError("plant", "name",
                    "Plant name must be under 255 characters");
        }

        return null;
    }

    /**
     * Validates the plant count when creating new Plant objects
     * @param count Plant count
     * @return FieldError if the count is not a positive number
     */
    public static FieldError validatePlantCount(String count) {
        if(count.length() > 255){
            return new FieldError("plant", "count", "Plant count must be a less than 255 characters");

        }
        if (!isPositiveNumber(count)) {
            return new FieldError("plant", "count", "Plant count must be a positive number");
        }
        return null;
    }

    /**
     * Validates the plant description when creating new Plant objects
     * @param description Plant description
     * @return FieldError if the description is longer than 512 characters
     */
    public static FieldError validatePlantDescription(String description) {
        boolean a = description.length() > 512;
        if (description.length() > 512) {
            return new FieldError("plant", "description", "Plant description must be less than 512 characters");
        }
        return null;
    }

    public static FieldError validatePlantDate(String date) {
        try {
            LocalDate datePlanted = LocalDate.parse(date);
            LocalDate today = LocalDate.now();
            Period period = Period.between(datePlanted, today);
            int plantAge = period.getYears();
            if (plantAge >= 400) {
                return new FieldError("plant", "datePlanted", "Date planted must be within the past 400 years.");
            } else if (plantAge < -200) {
                return new FieldError("plant", "datePlanted", "Date planted must be within the next 200 years.");
            } else {
                return null;
            }
            // Continue with further processing
        } catch (DateTimeParseException e) {
            return new FieldError("plant", "datePlanted", "Date is not in valid format, DD/MM/YYYY");

            // Handle the case where the date string doesn't match the expected format
        }
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
        String regex = "^[\\p{L}0-9\\s.,'-]+$";
        return Pattern.matches(regex, name);
    }
}
