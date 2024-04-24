package nz.ac.canterbury.seng302.gardenersgrove.validation;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * This class provides utility methods for validating user information.
 */
public class UserValidator {
    /**
     * Formats a name by trimming leading and trailing white spaces.
     * If the input name is null, it returns an empty string.
     *
     * @param name the name to be formatted
     * @return the formatted name, or an empty string if the input name is null
     */
    public static String formatName(String name) {
        if (name == null) {
            return "";
        } else {
            return name.trim();
        }
    }

    /**
     * Validates a user's name.
     *
     * @param name The name to validate.
     * @return true if the name is valid, false otherwise.
     */
    public static boolean isNameValid(String name) {
        boolean isValid = name.matches("[\\p{L}\\s'-]+");
        boolean hasMultipleSpaces = name.contains("  ");
        return isValid && !hasMultipleSpaces;
    }

    /**
     * Validates a user's email.
     * Checks for valid email format, length, whitespace and consecutive dots.
     * @param email The email to validate.
     * @return true if the email is valid, false otherwise.
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$") && !email.contains("..");
        boolean isWithinSqlLimit = email.length() <= 255;
        return isValid && isWithinSqlLimit;
    }

    /**
     * Converts a date from dd/mm/yyyy format to yyyy-mm-dd format.
     * If the input date is null or empty, it returns an empty string.
     *
     * @param dateInput The date to convert.
     * @return The converted date.
     */
    public static String convertDateFormat(String dateInput) {
        // If the date is null or empty, return an empty string
        if (dateInput == null || dateInput.isEmpty()) {
            return "";
        }
        // Convert the date to the correct format
        String[] parts = dateInput.split("/");
        if (dateInput.length() < 10) {
            return "0000-00-00";
        } else {
            String yyyy = parts[2];
            String mm = parts[1];
            String dd = parts[0];
            if (mm.length() == 1) {
                mm = "0" + mm;
            }
            if (dd.length() == 1) {
                dd = "0" + dd;
            }
            return yyyy + "-" + mm + "-" + dd;
        }
    }

    /**
     * Checks if a date of birth is valid.
     *
     * @param dateOfBirth The date of birth to check.
     * @return true if the date of birth is valid, false otherwise.
     */
    public static boolean checkDateValidity(String dateOfBirth) {
        try {
            LocalDate dob = LocalDate.parse(dateOfBirth);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Calculates a user's age from their date of birth.
     *
     * @param dateOfBirth The user's date of birth.
     * @return The user's age.
     */
    public static int calculateAge(String dateOfBirth) {
        LocalDate dob = LocalDate.parse(dateOfBirth);
        LocalDate today = LocalDate.now();
        Period period = Period.between(dob, today);
        int age = period.getYears();
        if (period.getMonths() < 0 || (period.getMonths() == 0 && today.getDayOfMonth() < dob.getDayOfMonth())) {
            age--;
        }
        return age;
    }

    /**
     * Validates a user's password.
     *
     * @param password The password to validate.
     * @return true if the password is valid, false otherwise.
     */
    public static boolean isPasswordValid(String password) {
        String specialCharacters = "[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]+";
        return password.length() >= 8 &&
                password.matches(".*\\d.*") &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                Pattern.compile(specialCharacters).matcher(password).find();
    }
}