package nz.ac.canterbury.seng302.gardenersgrove.unit.validation;

import nz.ac.canterbury.seng302.gardenersgrove.validation.GardenValidator;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.validation.ObjectError;
import static org.junit.jupiter.api.Assertions.*;

public class GardenValidatorTests {

    @Test
    public void ValidatingGardenName_EmptyString_Invalid() {
        ObjectError objectError = GardenValidator.validateGardenName("");
        assertNotNull(objectError);
    }

    @Test
    public void ValidatingGardenLocation_EmptyCountryAndCity_Invalid() {
        ObjectError objectError = GardenValidator.validateGardenLocation(new Location("", "", "", "", ""), true);
        assertNotNull(objectError);
    }

    @ParameterizedTest
    @CsvSource({"#Garden", "&&Garden", "Nice_Garden", "Cool._.Garden"})
    public void ValidatingGardenName_NonEmptyStrings_Invalid(String gardenName) {
        ObjectError objectError = GardenValidator.validateGardenName(gardenName);
        assertNotNull(objectError);
    }

    @ParameterizedTest
    @CsvSource({"#Location, ''", "'', &&Location", "'', ''"})
    public void ValidatingGardenLocation_NonEmptyStrings_Invalid(String city, String country) {
        ObjectError objectError = GardenValidator.validateGardenLocation(new Location("", "", "", country, city), true);
        assertNotNull(objectError);
    }

    @ParameterizedTest
    @CsvSource({"Cool Garden", "Garden123", "John Doe", "   Garden   ",})
    public void ValidatingGardenName_NonEmptyStrings_Valid(String gardenName) {
        ObjectError objectError = GardenValidator.validateGardenName(gardenName);
        assertNull(objectError);
    }

    @ParameterizedTest
    @CsvSource({"Timaru, New Zealand",})
    public void ValidatingGardenLocation_NonEmptyStrings_Valid(String gardenLocation) {
        ObjectError objectError = GardenValidator.validateGardenName(gardenLocation);
        assertNull(objectError);
    }

    @ParameterizedTest
    @CsvSource({"-1", "123..234", "123\\,\\,\\234", "" + Integer.MIN_VALUE})
    public void ValidatingGardenSize_NonEmptyStrings_Invalid(String gardenSize) {
        ObjectError objectError = GardenValidator.validateSize(gardenSize);
        assertNotNull(objectError);
    }

    @ParameterizedTest
    @CsvSource({"1", "0.00000000001", "" + Integer.MAX_VALUE})
    public void ValidatingGardenSize_NonEmptyStrings_Valid(String gardenSize) {
        ObjectError objectError = GardenValidator.validateSize(gardenSize);
        assertNull(objectError);
    }

    @Test
    public void ValidatingGardenName_WithPeriod_Valid() {
        String gardenName = "My.Garden";
        ObjectError objectError = GardenValidator.validateGardenName(gardenName);
        assertNull(objectError, "Garden name with period should be valid");
    }

    @ParameterizedTest
    @CsvSource({"sfcrtrbunrjqdpqsqdcfnunpzmatzvqwlhmouukyjzymuydxkmqzflupmqtypiupgupetidvkicweljpzlkufqgerlapwkuhl" +
            "uvsmbptsdyvdgpoxvkrbaaelpfnmdgtlwkyuigtqnciuzzviobgeisyeqgtdiumxwumgtuhwlnkdtgpfvbpzugncscningysdlauvf" +
            "vdrbhgwwhbstpabjddabjibvsrjkrgbjeyqvzlrzyxvcinjyglesyucft"})
    public void ValidatingGardenLocation_CityLength_Invalid(String city) {
        ObjectError objectError = GardenValidator.validateGardenLocation(new Location("", "", "", "", city), true);
        assertNotNull(objectError);
    }

    @ParameterizedTest
    @CsvSource({"sfcrtrbunrjqdpqsqdcfnunpzmatzvqwlhmouukyjzymuydxkmqzflupmqtypiupgupetidvkicweljpzlkufqgerlapwkuhl" +
            "uvsmbptsdyvdgpoxvkrbaaelpfnmdgtlwkyuigtqnciuzzviobgeisyeqgtdiumxwumgtuhwlnkdtgpfvbpzugncscningysdlauvf" +
            "vdrbhgwwhbstpabjddabjibvsrjkrgbjeyqvzlrzyxvcinjyglesyucft"})
    public void ValidatingGardenLocation_CountryLength_Invalid(String country) {
        ObjectError objectError = GardenValidator.validateGardenLocation(new Location("", "", country, "", ""), false);
        assertNotNull(objectError);
    }
}
