package nz.ac.canterbury.seng302.gardenersgrove.unit.validation;

import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.validation.TipValidator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpSession;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TipValidatorTests {


    @ParameterizedTest
    @CsvSource({
            "1, 1, true",
            "10, 10, true",
            "1111111, 1, false",
            "0, 1111111, false",
            "-1, 0, false",
    })
    public void testDoTipValidations(Integer tipAmount, Integer userBalance, boolean expectedResult) {
        User user = new User();
        user.setBloomBalance(userBalance);
        HttpSession session = new MockHttpSession();

        TipValidator.doTipValidations(session, tipAmount, user);

        if (expectedResult) {
            assertNull(session.getAttribute("tipAmountError"));
        } else {
            assertNotNull(session.getAttribute("tipAmountError"));
        }
    }
}