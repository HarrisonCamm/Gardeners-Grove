package nz.ac.canterbury.seng302.gardenersgrove.unit.validation;
import nz.ac.canterbury.seng302.gardenersgrove.validation.LocationValidator;
import org.springframework.validation.FieldError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

public class LocationValidatorTests {

    @ParameterizedTest
    @CsvSource({"4 Kotare", "32 Clyde", "59 Lochee Rd", "4 Charente Way"})
    public void ValidatingStreetAddress_Valid(String address) {
        FieldError fieldError = LocationValidator.validateStreetAddress(address);
        assertNull(fieldError);
    }

    @ParameterizedTest
    @CsvSource({"123 Main South Rd.!@#", "56 Prevel St<>", "&*@(#$"})
    public void ValidatingStreetAddress_Invalid(String address) {
        FieldError fieldError = LocationValidator.validateStreetAddress(address);
        assertNotNull(fieldError);
    }

    @ParameterizedTest
    @CsvSource({"yaldy", "ricc", "Hornby", "Bishopdale"})
    public void ValidatingSuburb_Valid(String suburb) {
        FieldError fieldError = LocationValidator.validateSuburb(suburb);
        assertNull(fieldError);
    }

    @ParameterizedTest
    @CsvSource({"_==-@#", "___332$$%<>", "&*#*@$"})
    public void ValidatingSuburb_Invalid(String suburb) {
        FieldError fieldError = LocationValidator.validateSuburb(suburb);
        assertNotNull(fieldError);
    }

    @ParameterizedTest
    @CsvSource({"1234", "6789"})
    public void ValidatingPostcode_Valid(String postcode) {
        FieldError fieldError = LocationValidator.validatePostcode(postcode);
        assertNull(fieldError);
    }

    @ParameterizedTest
    @CsvSource({"ABCDE", "1234A", "12 345"})
    public void ValidatingPostcode_Invalid(String postcode) {
        FieldError fieldError = LocationValidator.validatePostcode(postcode);
        assertNotNull(fieldError);
    }

    @ParameterizedTest
    @CsvSource({"San Francisco", "New York", "Los Angeles", "Chicago"})
    public void ValidatingCity_Valid(String city) {
        FieldError fieldError = LocationValidator.validateCity(city);
        assertNull(fieldError);
    }

    @ParameterizedTest
    @CsvSource({"San Francisco!@#", "New York<>", "BadCity$"})
    public void ValidatingCity_Invalid(String city) {
        FieldError fieldError = LocationValidator.validateCity(city);
        assertNotNull(fieldError);
    }

    @ParameterizedTest
    @CsvSource({"United States", "Canada", "Mexico", "Brazil"})
    public void ValidatingCountry_Valid(String country) {
        FieldError fieldError = LocationValidator.validateCountry(country);
        assertNull(fieldError);
    }

    @ParameterizedTest
    @CsvSource({"United States!@#", "Canada<>", "BadCountry$"})
    public void ValidatingCountry_Invalid(String country) {
        FieldError fieldError = LocationValidator.validateCountry(country);
        assertNotNull(fieldError);
    }

}
