package nz.ac.canterbury.seng302.gardenersgrove.unit.validation;

import nz.ac.canterbury.seng302.gardenersgrove.validation.TagValidator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

public class TagValidatorTests {

    @ParameterizedTest
    @CsvSource({
            "ValidTag, true",
            "Invalid Tag!, false",
            "Another_Valid-Tag, true",
            "Invalid@Tag, false"
    })
    public void testIsValidTag(String tag, boolean expected) {
        assertEquals(expected, TagValidator.isValidTag(tag));
    }

    @ParameterizedTest
    @CsvSource({
            "null, true",
            "Shit, false"
    })
    public void testIsAppropriateName(String possibleTerms, boolean expected) {
        assertEquals(expected, TagValidator.isAppropriateName(possibleTerms));
    }

    @ParameterizedTest
    @CsvSource({
            "shortone, true",
            "exactly25CHARSnopqrstuvwy, true",
            "ThisTagIsATestToSeeIfTheTagIsWayTooLongLikeAMarathon, false"
    })
    public void testIsValidLength(String tag, boolean expected) {
        assertEquals(expected, TagValidator.isValidLength(tag));
    }

}