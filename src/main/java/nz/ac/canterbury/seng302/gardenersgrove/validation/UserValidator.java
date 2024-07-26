package nz.ac.canterbury.seng302.gardenersgrove.validation;

import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.service.UserService;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * This class provides utility methods for validating user information.
 */
public class UserValidator {
    private static final int MAX_NAME_LENGTH = 64;
    private static final String PASSWORD_ERROR = "Your password must be at least 8 characters long and include at  " +
            "least one uppercase letter, one lowercase letter, one number, and one special character.";

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
     * Checks for valid characters and no spaces.
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

        if (!dateInput.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return "0000-00-00";
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
     * Checks if a date is valid.
     *
     * @param date The date of birth to check.
     * @return true if the date of birth is valid, false otherwise.
     */
    public static boolean checkDateValidity(String date) {
        try {
            LocalDate dob = LocalDate.parse(date);
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
     * Ensures the password is at least 8 characters long,
     * contains at least one uppercase letter, one lowercase letter, one digit and one special character.
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

    /**
     * Checks if the password contains the user's details. (For AC4)
     * @param user logged in user
     * @param password users new password that is being validated
     * @return true if the password contains the user's details, false otherwise
     */
    public static boolean passwordContainsDetails(User user, String password) {

        // Check for first name
        boolean containsFirstName = password.toLowerCase().contains(user.getFirstName().toLowerCase());
        // Check for last name, handles no last name case too
        boolean containsLastName = !user.getLastName().isEmpty() && password.toLowerCase().contains(user.getLastName().toLowerCase());
        // Check for email
        boolean containsEmail = password.toLowerCase().contains(user.getEmail().toLowerCase());

        // Check for date of birth
        String dob = user.getDateOfBirth();
        String formattedDob = convertDateFormat(dob);
        boolean containsDOB = false;
        // Handel empty DOB
        if (!formattedDob.equals("0000-00-00") && !formattedDob.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            try {
                LocalDate dateOfBirth = LocalDate.parse(dob, formatter);
                String formattedDobString = dateOfBirth.format(formatter);
                // Check if the password contains the date of birth
                containsDOB = password.contains(formattedDobString);
            } catch (DateTimeParseException e) {
                // Handle parsing error
                containsDOB = false;
            }
        }

        return containsFirstName || containsLastName || containsEmail || containsDOB;
    }

    /**
     * Checks if the input is not null and not empty.
     * @param input the input to check
     * @return true if the input is not null and not empty, false otherwise
     */
    public static boolean hasInput(String input) {
        return input != null && !input.isEmpty();
    }

    /**
     * Validates a user's details for registering a new user and editing an existing user.
     * @param model the model to add error messages to
     * @param userService the user service to check if the email already exists
     * @param user the user to check if the email already exists if logged in, otherwise null
     * @param email the email to validate
     * @param noLastName true if the user has no last name, false otherwise
     * @param firstName the first name to validate
     * @param lastName the last name to validate
     * @param formattedDob the date of birth to validate
     */
    public static void doUserValidations(Model model, UserService userService, User user, String email,
        boolean noLastName, String firstName, String lastName, String formattedDob) {

        // Checks if the email is input and valid
        if (!hasInput(email) || !isEmailValid(email)) {
            model.addAttribute("registrationEmailError", "Email address must be in the form ‘jane@doe.nz’");
        }
        // Checks if the email already exists and is not the user's email if a user is given
        else if (userService.emailExists(email) && (user == null || !email.equals(user.getEmail()))) {
            model.addAttribute("registrationEmailError", "This email address is already in use");
        }

        // Checks if the first name is input and valid
        if (!hasInput(firstName)) {
            model.addAttribute("firstNameError", "First name cannot be empty");
        }
        else {
            if (firstName.length() > MAX_NAME_LENGTH) {
                model.addAttribute("firstNameError", "First name must be 64 characters long or less");
            }
            if (!isNameValid(firstName)) {
                model.addAttribute("firstNameError", "First name must only include letters, spaces, hyphens or apostrophes");
            }
        }

        // Checks if the last name is input and valid if the user has a last name
        if (!noLastName) {
            if (!hasInput(lastName)) {
                model.addAttribute("lastNameError", "Last name cannot be empty");
            }
            if (lastName.length() > MAX_NAME_LENGTH) {
                model.addAttribute("lastNameError", "Last name must be 64 characters long or less");
            }
            if (!isNameValid(lastName)) {
                model.addAttribute("lastNameError", "Last name must only include letters, spaces, hyphens or apostrophes");
            }
        }

        // If present, checks if the date of birth is valid
        if (hasInput(formattedDob)) {
            if (!checkDateValidity(formattedDob)) {
                model.addAttribute("ageError", "Date in not in valid format, DD/MM/YYYY");
            } else {
                if (calculateAge(formattedDob) < 13) {
                    model.addAttribute("ageError", "You must be 13 years old or older to create an account");
                }
                if (calculateAge(formattedDob) > 120) {
                    model.addAttribute("ageError", "The maximum age allowed is 120 years");
                }
            }
        }
    }

    /**
     * Validates a user's password. If a user is not logged in, the old password is not validated.
     * @param model the model to add error messages to
     * @param userService the user service to validate the user's old password
     * @param user the user to validate the old password for, or null if the user is not logged in
     * @param oldPassword the old password to validate
     * @param newPassword the new password to validate
     * @param retypePassword the retyped password to validate
     */
    public static void doPasswordValidations(Model model, UserService userService, User user, String oldPassword,
                                             String newPassword, String retypePassword) {
        // Check if the user is logged in
        if (user != null) {
            // Check if the old password is empty
            if (!hasInput(oldPassword)) {
                model.addAttribute("oldPasswordError", PASSWORD_ERROR);
            } else {
                // Attempt to validate the user with the provided old password
                Optional<User> validatedUser = userService.validateUser(user.getEmail(), oldPassword);

                // If the Optional is empty, the old password does not match
                if (validatedUser.isEmpty()) {
                    model.addAttribute("oldPasswordError", "Your old password is incorrect");
                }
            }
        }

        // Check if the new password is empty
        if (!hasInput(newPassword)) {
            model.addAttribute("newPasswordError", PASSWORD_ERROR);
        } else {
            // Validate the new password strength and check if it contains user details
            if (!isPasswordValid(newPassword) || (user != null && passwordContainsDetails(user, newPassword))) {
                model.addAttribute("newPasswordError", PASSWORD_ERROR);
            }
        }

        // Check if the retyped password is empty
        if (!hasInput(retypePassword)) {
            model.addAttribute("passwordMatchError", PASSWORD_ERROR);
        } else {
            // Check if the new password and retype password match
            if (!newPassword.equals(retypePassword)) {
                model.addAttribute("passwordMatchError", "The new passwords do not match");
            }
        }
    }
}