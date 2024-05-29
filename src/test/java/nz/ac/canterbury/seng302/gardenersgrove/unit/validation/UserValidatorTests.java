package nz.ac.canterbury.seng302.gardenersgrove.unit.validation;

import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidator.*;


/**
 * Tests for validating the functionality of name validation and formatting.
 */
public class UserValidatorTests {
    /**
     * Test to verify that the formatName method correctly trims leading and trailing whitespace
     * from input names and handles null inputs by returning an empty string.
     * This test covers cases including strings with excessive whitespace, strings with no
     * whitespace, and null inputs.
     */
    @ParameterizedTest
    @CsvSource({
            "'  John Doe  ', John Doe", // Leading/trailing spaces are trimmed
            "Jane, Jane", // No changes needed, no spaces to trim
            "'    ', ''", // Only spaces, all are trimmed
            "'  Multiple   Spaces  ', 'Multiple   Spaces'" // Trim does not affect internal multiple spaces,
            // caught by isNameValid instead
    })
    void testFormatName(String input, String expected) {
        assertEquals(expected, formatName(input));
    }

    /**
     * Test to verify that the formatName method returns an empty string when the input is null.
     */
    @ParameterizedTest
    @NullSource
    void testFormatNameWithNullInput(String input) {
        assertEquals("", formatName(input));
    }

    /**
     * Test to verify that the isNameValid method correctly identifies valid and invalid names.
     * This test covers a variety of cases including valid alphabetic names with acceptable characters
     * such as hyphens, apostrophes, and macrons, as well as invalid names containing numbers, special characters,
     * multiple spaces, and empty strings.
     */
    @ParameterizedTest
    @CsvSource({
            "John Doe, true", // Valid: only alphabetic characters and a space
            "Anne-Marie O'Neill, true", // Valid: includes hyphen and apostrophe
            "Māori Name, true", // Valid: includes macron and space
            "Jean-Luc, true", // Valid: includes hyphen
            "John123, false", // Invalid: contains numeric characters
            "Special@Name, false", // Invalid: contains special character '@'
            "Consecutive  Spaces, false", // Invalid: contains multiple consecutive spaces
            "'', false" // Invalid: empty string
    })
    void testIsNameValid(String input, boolean expected) {
        assertEquals(expected, isNameValid(input));
    }

    /**
     * Test cases for valid and invalid email addresses.
     * Each test case includes the email to be tested and the expected outcome (true for valid, false for invalid).
     */
    @ParameterizedTest
    @CsvSource({
            // Valid email cases
            "jane@doe.nz, true",
            "john.smith@company.co.uk, true",
            "first.last@iana.org, true",

            // Invalid email cases
            "'', false", // Empty string
            "jane@doe, false", // Missing top-level domain
            "jane@.com, false", // No second-level domain
            "jane@doe., false", // Dot at the end of the domain
            "jane@doe.com., false", // Trailing dot
            "jane@doe..com, false", // Double dots in the domain
            "@doe.com, false", // Missing prefix before the "@"
            "jane.doe.com, false", // Missing "@" symbol
            "jane@doe_-.com, false", // Invalid characters in the domain
            "jane doe@domain.com, false", // Spaces in the email
            "jane@doe@doe.com, false", // Multiple "@" symbols
    })
    public void testEmailValidation(String email, boolean expected) {
        assertEquals(expected, isEmailValid(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // Invalid email cases
            " jane@doe.com", // Leading whitespace
            "jane@doe.com ", // Trailing whitespace
            " jane@doe.com " }) // Leading and trailing whitespace
    public void testEmailWithWhitespace(String email) {
        // All cases should be invalid
        assertFalse(isEmailValid(email));
    }

    /**
     * Provides test data for testing the email length limit.
     */
    private static Stream<Object[]> emailLengthTestData() {
        String base = "a".repeat(243); // 243 'a's
        String domain = "@example.com"; // 12 characters
        return Stream.of(
                new Object[] {base + domain, true},  // 255 characters, valid
                new Object[] {base + "a" + domain, false},  // 256 characters, invalid
                new Object[] {base.substring(1) + domain, true}  // 254 characters, valid
        );
    }

    /**
     * Tests email validation with respect to the character length limits.
     */
    @ParameterizedTest
    @MethodSource("emailLengthTestData")
    public void testEmailLengthLimit(String email, boolean expected) {
        assertEquals(expected, isEmailValid(email));
    }

    /**
     * Test to verify that a date is converted from dd/mm/yyyy format to yyyy-mm-dd format.
     * This test covers a variety of cases including valid dates, invalid dates, and empty inputs.
     * Front-end covers invalid format case
     * Another function covers if the entered date is a legitimate date
     *
     * @param input The date string to be converted.
     * @param expected The expected result after conversion.
     */
    @ParameterizedTest
    @CsvSource({
            // Test valid inputs
            "01/01/2020, 2020-01-01",
            "31/12/1999, 1999-12-31",
            "29/02/2020, 2020-02-29", // Leap year date

            // Test invalid inputs
            "01/01/20, 0000-00-00",   // Invalid year format
            "13/2020, 0000-00-00",    // Missing day

            // Test empty inputs
            "'', ''",                 // Empty string input
            " , ''",                  // Space as input
    })
    public void testConvertDateFormat(String input, String expected) {
        assertEquals(expected, convertDateFormat(input));
    }

    /**
     * Test to verify that the convertDateFormat method correctly handles null inputs.
     *
     * @param input The date string to be converted.
     */
    @ParameterizedTest
    @NullSource
    public void testConvertDateFormatWithNullInput(String input) {
        assertEquals("", convertDateFormat(input));
    }

    /**
     * This class is designed to test the checkDateValidity function.
     * Ensures that dates after being formatted are legitimate valid dates
     * Front-end prevents a Non-ISO format being entered
     */
    @ParameterizedTest
    @CsvSource({
            // Test valid inputs
            "2020-01-01, true",
            "1999-12-31, true",
            "2020-02-29, true", // Valid leap year date

            // Test invalid date formats
            "01-01-2020, false",  // Invalid format (DD-MM-YYYY)
            "2020/01/01, false",  // Invalid format (YYYY/MM/DD)
            "2020.01.01, false",  // Invalid format (YYYY.MM.DD)
            "31-12-1999, false",  // Invalid format (DD-MM-YYYY)

            // Test incorrect dates
            "2020-02-30, false", // Invalid day for February
            "2020-04-31, false", // April has only 30 days
    })
    public void testCheckDateValidity(String input, boolean expected) {
        assertEquals(expected, checkDateValidity(input));
    }

    /**
     * Modified version of calculateAge for testing purposes
     * Modified to take a Clock object as a parameter to set the localDate
     *
     * @param dateOfBirth The user's date of birth.
     * @param clock       The clock to use for determining the current date, useful for testing.
     * @return The user's age.
     */
    private static int modifiedCalculateAge(String dateOfBirth, Clock clock) {
        LocalDate dob = LocalDate.parse(dateOfBirth);
        LocalDate today = LocalDate.now(clock);
        Period period = Period.between(dob, today);
        int age = period.getYears();
        if (period.getMonths() < 0 || (period.getMonths() == 0 && period.getDays() < 0)) {
            age--;
        }
        return age;
    }

    /**
     * Test to verify that the calculateAge method correctly calculates a user's age based on their date of birth.
     * This test covers a variety of cases including exact birthdays, days before and after birthdays, and leap years.
     *
     * @param dob The user's date of birth in yyyy-mm-dd format.
     * @param today The current date in yyyy-mm-dd format.
     * @param expected The expected age of the user.
     */
    @ParameterizedTest
    @CsvSource({
            "2000-01-01, 2022-01-01, 22", // Exact birthday
            "2000-12-31, 2022-12-30, 21", // Day before birthday
            "2000-12-31, 2023-01-01, 22", // Day after birthday
            "2004-02-29, 2023-02-28, 18", // Leap year, day before birthday
            "2004-02-29, 2024-02-29, 20", // Leap year, on birthday
            "2004-02-29, 2024-03-01, 20"  // Leap year, day after birthday
    })
    public void testCalculateAge(String dob, String today, int expected) {
        Clock fixedClock = Clock.fixed(LocalDate.parse(today).atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        assertEquals(expected, modifiedCalculateAge(dob, fixedClock));
    }

    /**
     * Test whether the password validation logic conforms to specified security standards.
     *
     * @param password The password to validate.
     * @param expected The expected outcome of the validation.
     */
    @ParameterizedTest
    @CsvSource({
            // Valid password
            "'Passw0rd@', true",

            // Test passwords that fail specific conditions
            "'password', false",  // No upper case, no special character, no digit
            "'PASSWORD', false",  // No lower case, no special character, no digit
            "'Password', false",  // No special character, no digit
            "'Passw0rd', false",  // No special character
            "'P@ssword', false",  // No digit
            "'passw0rd@', false", // No upper case
            "'PASSW0RD@', false", // No lower case
            "'Pa@1', false",      // Less than 8 characters

            // Edge cases
            "'!@#$%^&*', false",  // Only special characters
            "'12345678', false",  // Only digits
            "'abcdefgh', false",  // Only lower case letters
            "'ABCDEFGH', false",  // Only upper case letters
            "'AbCdEfGh', false",  // Missing digit and special character
            "'ABCD!@#$', false",  // Missing lower case and digit
            "'1234!@#$', false",  // Missing letters
            "'', false",          // Empty string
            "'         ', false", // Spaces only
            "'Passw0rd@@@@@@@@@@@', true"  // Long with all requirements
    })
    public void testPasswordValidity(String password, boolean expected) {
        assertEquals(expected, isPasswordValid(password));
    }


    @ParameterizedTest
    @CsvSource({
            // Valid Password
            "Passw0rd@, false",

            // Invalid with form information at start
            "JohnPassw0rd@, true", // Contains first name
            "DoePassw0rd@, true", // Contains last name
            "john.doe@email.comPassw0rd@, true", // Contains email
            "17/10/2003Passw0rd@, true", // Contains DOB

            // Invalid with form information in the middle
            "PassJohnw0rd@, true", // Contains first name
            "PassDoew0rd@, true", // Contains last name
            "Passjohn.doe@email.comw0rd@, true", // Contains email
            "Pass17/10/2003w0rd@, true", // Contains DOB

            // Invalid with form information at the end
            "Passw0rd@John, true", // Contains first name
            "Passw0rd@Doe, true", // Contains last name
            "Passw0rd@john.doe@email.com, true", // Contains email
            "Passw0rd@17/10/2003, true", // Contains DOB
    })
    public void testPasswordContainsDetails(String password, boolean expected) {
        User user = new User("john.doe@email.com", "John", "Doe", "Passw0rd@", "17/10/2003");
        assertEquals(expected, passwordContainsDetails(user, password));
    }
}
