package nz.ac.canterbury.seng302.gardenersgrove.validation;

import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class PlantValidator {
    private final static int MIN_AGE = -200;
    private final static int MAX_AGE = 14000;

    private final static int MAX_NAME_LENGTH = 255;
    private final static int MAX_DESCRIPTION_LENGTH = 512;

    private final static int MAX_PLANT_COUNT = 255;

    private static FieldErrorFactory fieldErrorFactory = new FieldErrorFactory();

    /**
     * Get the FieldErrorFactory object
     * @return FieldErrorFactory object
     */
    public static FieldErrorFactory getFieldErrorFactory() {
        return fieldErrorFactory;
    }

    /**
     * Set the FieldErrorFactory object
     * @param newFieldErrorFactory FieldErrorFactory object
     */
    public static void setFieldErrorFactory(FieldErrorFactory newFieldErrorFactory) {
        fieldErrorFactory = newFieldErrorFactory;
    }

    /**
     * Validates the plant name when creating new Plant objects
     * @param name Plant name
     * @return FieldError if the plant name is empty or contains invalid characters
     */
    public static FieldError validatePlantName(String name) {
        if (name == null || name.isEmpty() || name.trim().isEmpty() || !isValidPlantName(name)) {
            return fieldErrorFactory.createFieldError("plant", "name",
                    "Plant name cannot be empty and must only include letters, numbers, spaces, dots, " +
                            "hyphens or apostrophes.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            return fieldErrorFactory.createFieldError("plant", "name", "Plant name must be less than " + MAX_NAME_LENGTH + " characters");
        }
        return null;
    }

    /**
     * Validates the plant count when creating new Plant objects
     * @param count Plant count
     * @return FieldError if the count is not a positive number
     */
    public static FieldError validatePlantCount(String count) {
        if (!isPositiveNumber(count)) {
            return fieldErrorFactory.createFieldError("plant", "count", "Plant count must be a positive number");
        }
        if (count.length() > MAX_PLANT_COUNT) {
            return fieldErrorFactory.createFieldError("plant", "count", "Plant count must be less than 255 characters");
        }
        return null;
    }

    /**
     * Validates the plant description when creating new Plant objects
     * @param description Plant description
     * @return FieldError if the description is longer than 512 characters
     */
    public static FieldError validatePlantDescription(String description) {
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            return fieldErrorFactory.createFieldError("plant", "description", "Plant description must be less than " + MAX_DESCRIPTION_LENGTH + " characters");
        }
        return null;
    }

    public static FieldError validatePlantDate(String date) {
        try {
            LocalDate datePlanted = LocalDate.parse(date);
            LocalDate today = LocalDate.now();
            Period period = Period.between(datePlanted, today);
            int plantAge = period.getYears();       // Get the difference between today and the date planted,
                                                    // can be negative indicated the plant will be planted in the future
            if (plantAge >= MAX_AGE) {
                return fieldErrorFactory.createFieldError("plant", "datePlanted", "Date planted must be within the past " + MAX_AGE + "years.");
            } else if (plantAge < MIN_AGE) {
                // Checks if the plant is planted in the future
                // Since plantAge is negative, Math.abs() is used to get the positive value to display in the error message
                return fieldErrorFactory.createFieldError("plant", "datePlanted", "Date planted must be within the next " + Math.abs(MIN_AGE) + " years.");
            } else {
                return null;
            }
            // Continue with further processing
        } catch (DateTimeParseException e) {
            return fieldErrorFactory.createFieldError("plant", "datePlanted", "Date is not in valid format, DD/MM/YYYY");
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
