package nz.ac.canterbury.seng302.gardenersgrove.unit.validation;

import nz.ac.canterbury.seng302.gardenersgrove.validation.GardenValidator;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Location;
import nz.ac.canterbury.seng302.gardenersgrove.validation.LocationValidator;
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

    @ParameterizedTest
    @CsvSource({"#Garden", "&&Garden", "Nice_Garden", "Cool._.Garden", "Hype()"})
    public void ValidatingGardenName_NonEmptyStrings_Invalid(String gardenName) {
        ObjectError objectError = GardenValidator.validateGardenName(gardenName);
        assertNotNull(objectError);
    }

    @ParameterizedTest
    @CsvSource(value = {"Cool, Garden", "Garden,123", "John,Doe", "   Garden,Cool,Name,Aakrista   ",}, delimiter = ';')
    public void ValidatingGardenName_IncludesComma_Valid( String gardenName) {
        ObjectError objectError = GardenValidator.validateGardenName(gardenName);
        assertNull(objectError);
    }

    @ParameterizedTest
    @CsvSource({"Cool Garden",
            "Garden123",
            "John Doe",
            "   Garden   ",
            "Oliver is cool-",
            "'Oliver is cool'",
            "-Oliver",
            "Oli-ver",
            "Oli'ver" +
            "Oli.ver" +
            ".Oli'ver." +
            ".Oliver"
        })
    public void ValidatingGardenName_NonEmptyStrings_Valid(String gardenName) {
        ObjectError objectError = GardenValidator.validateGardenName(gardenName);
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
        ObjectError objectError = LocationValidator.validateCity(city);
        assertNotNull(objectError);
    }

    @ParameterizedTest
    @CsvSource({"sfcrtrbunrjqdpqsqdcfnunpzmatzvqwlhmouukyjzymuydxkmqzflupmqtypiupgupetidvkicweljpzlkufqgerlapwkuhl" +
            "uvsmbptsdyvdgpoxvkrbaaelpfnmdgtlwkyuigtqnciuzzviobgeisyeqgtdiumxwumgtuhwlnkdtgpfvbpzugncscningysdlauvf" +
            "vdrbhgwwhbstpabjddabjibvsrjkrgbjeyqvzlrzyxvcinjyglesyucft"})
    public void ValidatingGardenLocation_CountryLength_Invalid(String country) {
        ObjectError objectError = LocationValidator.validateCity(country);
        assertNotNull(objectError);
    }
}
