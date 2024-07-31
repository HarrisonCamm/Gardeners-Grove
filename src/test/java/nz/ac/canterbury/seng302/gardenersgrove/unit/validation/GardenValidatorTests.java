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

    private static final String SURFACE_AREA_OF_EARTH = "510100000";

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
            ".Oliver" +
            "Oliver."
        })
    public void ValidatingGardenName_NonEmptyStrings_Valid(String gardenName) {
        ObjectError objectError = GardenValidator.validateGardenName(gardenName);
        assertNull(objectError);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "-1",
            "123..234",
            "123,,234",
            "" + Integer.MIN_VALUE,
            "" + Integer.MAX_VALUE,
            SURFACE_AREA_OF_EARTH + ".1",
            SURFACE_AREA_OF_EARTH + ",1",
            "10000000000000000,111",
            "000000000000000000000000000" +
                    "0000000000000000000" +
                    "0000000000000000000" +
                    "0000000000000000000" +
                    "0000000000000000000" +
                    "0000000000000000000" +
                    "0000000000000000000" +
                    "0000000000000000000" +
                    "0000000000000000000" +
                    "0000000000000000000" +
                    "0000000000000000000" +
                    "0000000000000000000" +
                    "00000000000000000001"
    }, delimiter = ';') //Changed delimiter for allowing comma test cases
    public void ValidatingGardenSize_NonEmptyStrings_Invalid(String gardenSize) {
        ObjectError objectError = GardenValidator.validateSize(gardenSize);
        assertNotNull(objectError);
    }

    @ParameterizedTest
    @CsvSource(value = {"1", "0.00000000001", SURFACE_AREA_OF_EARTH, "1,1111111"}, delimiter = ';')
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
    @CsvSource({
            "Java is a versatile and widely-used programming language. It was designed to be platform-independent, meaning it can run on any device that supports Java without needing to be recompiled. Java is known for its simplicity, readability, and maintainability, making it popular among developers for building applications ranging from mobile apps to enterprise-level systems. The language's syntax is similar to C and C++, making it relatively easy for programmers familiar with those languages to transition to Java.\\n\\n" +
                    "Java programs are typically compiled to bytecode, which can then be executed by any Java Virtual Machine (JVM). This bytecode allows Java applications to run on different platforms seamlessly, making it a preferred choice for cross-platform development. Java's extensive standard library provides developers with a rich set of tools and frameworks for various tasks, such as networking, database connectivity, and user interface development.\\n\\n" +
                    "One of Java's key features is its robust security model, which protects systems from harmful code. Java applications run within a sandbox environment that restricts their access to system resources unless explicitly allowed. This security model has contributed to Java's widespread adoption in environments where security is a priority, such as banking and e-commerce.\\n\\n" +
                    "Java's object-oriented nature promotes modular and reusable code through classes and objects. This approach allows developers to build complex applications by combining and extending existing code components. Additionally, Java supports multithreading, enabling concurrent execution of tasks within a single program, which is essential for performance-intensive applications.\\n\\n" +
                    "In recent years, Java has evolved with new features and updates, maintaining its relevance in a rapidly changing technological landscape. The introduction of lambda expressions in Java 8 enhanced its functional programming capabilities, while Java 11 introduced long-term support (LTS) versions, providing stability for enterprise deployments.\\n\\n" +
                    "Overall, Java continues to be a cornerstone of modern software development due to its versatility, performance, and extensive ecosystem. Whether you're developing mobile apps, web applications, or backend services, Java offers the tools and capabilities needed to build robust and scalable solutions."
    })
    public void ValidatingGardenLocation_CountryLength_Invalid(String country) {
        ObjectError objectError = LocationValidator.validateCity(country);
        assertNotNull(objectError);
    }
}
