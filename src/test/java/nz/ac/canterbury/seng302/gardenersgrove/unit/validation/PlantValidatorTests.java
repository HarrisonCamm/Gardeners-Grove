package nz.ac.canterbury.seng302.gardenersgrove.unit.validation;

import nz.ac.canterbury.seng302.gardenersgrove.validation.LocationValidator;
import nz.ac.canterbury.seng302.gardenersgrove.validation.PlantValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.validation.ObjectError;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PlantValidatorTests {

    @ParameterizedTest
    @CsvSource(value = {
            "-1",
            "" + Long.MIN_VALUE,
            "'1.1.'",
            "'1..1'",
            "'1,,1'",
            "'1,1,'",
            "'-1.1.'",
            "'-1..1'",
            "'-1,,1'",
            "'-1,1,'"}, delimiter = ';')
    public void ValidatingPlantCount_InvalidField_HasErrors(String plantCount) {
        ObjectError objectError = PlantValidator.validatePlantCount(plantCount);
        assertNotNull(objectError);
    }

    @ParameterizedTest
    @CsvSource({"1", "" + Integer.MAX_VALUE})
    public void ValidatingPlantCount_ValidField_NoErrors(String plantCount) {
        ObjectError objectError = PlantValidator.validatePlantCount(plantCount);
        assertNull(objectError);
    }


    @ParameterizedTest
    @CsvSource({"A cool plant", "''"})
    public void ValidatingPlantDescription_ValidField_NoErrors(String plantDescription) {
        ObjectError objectError = PlantValidator.validatePlantDescription(plantDescription);
        assertNull(objectError);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Java is a versatile and widely-used programming language. It was designed to be platform-independent, meaning it can run on any device that supports Java without needing to be recompiled. Java is known for its simplicity, readability, and maintainability, making it popular among developers for building applications ranging from mobile apps to enterprise-level systems. The language's syntax is similar to C and C++, making it relatively easy for programmers familiar with those languages to transition to Java.\\n\\n" +
                    "Java programs are typically compiled to bytecode, which can then be executed by any Java Virtual Machine (JVM). This bytecode allows Java applications to run on different platforms seamlessly, making it a preferred choice for cross-platform development. Java's extensive standard library provides developers with a rich set of tools and frameworks for various tasks, such as networking, database connectivity, and user interface development.\\n\\n" +
                    "One of Java's key features is its robust security model, which protects systems from harmful code. Java applications run within a sandbox environment that restricts their access to system resources unless explicitly allowed. This security model has contributed to Java's widespread adoption in environments where security is a priority, such as banking and e-commerce.\\n\\n" +
                    "Java's object-oriented nature promotes modular and reusable code through classes and objects. This approach allows developers to build complex applications by combining and extending existing code components. Additionally, Java supports multithreading, enabling concurrent execution of tasks within a single program, which is essential for performance-intensive applications.\\n\\n" +
                    "In recent years, Java has evolved with new features and updates, maintaining its relevance in a rapidly changing technological landscape. The introduction of lambda expressions in Java 8 enhanced its functional programming capabilities, while Java 11 introduced long-term support (LTS) versions, providing stability for enterprise deployments.\\n\\n" +
                    "Overall, Java continues to be a cornerstone of modern software development due to its versatility, performance, and extensive ecosystem. Whether you're developing mobile apps, web applications, or backend services, Java offers the tools and capabilities needed to build robust and scalable solutions."
    }, delimiter = ';')
    public void ValidatingPlant_PlantDescription_Invalid(String country) {
        ObjectError objectError = PlantValidator.validatePlantDescription(country);
        assertNotNull(objectError);
    }

    @ParameterizedTest
    @CsvSource(value = {"sfcrtrbunrjqdpqsqdcfnunpzmatzvqwlhmouukyjzymuydxkmqzflupmqtypiupgupetidvkicweljpzlkufqgerlapwkuhl" +
            "uvsmbptsdyvdgpoxvkrbaaelpfnmdgtlwkyuigtqnciuzzviobgeisyeqgtdiumxwumgtuhwlnkdtgpfvbpzugncscningysdlauvf" +
            "vdrbhgwwhbstpabjddabjibvsrjkrgbjeyqvzlrzyxvcinjyglesyucft",
            "__",
            "XXX_PlantName_XXX",
            "$$ROSE$$$"}, delimiter = ';')
    public void ValidatingPlant_PlantName_Invalid(String name) {
        ObjectError objectError = PlantValidator.validatePlantName(name);
        assertNotNull(objectError);
    }

    @ParameterizedTest
    @ValueSource(strings = {""})
    @NullAndEmptySource
    public void ValidationPlant_PlantNameEmptyAndNull_Invalid(String name) {
        ObjectError objectError = PlantValidator.validatePlantName(name);
        assertNotNull(objectError);
    }

    @ParameterizedTest
    @CsvSource({"Rose", "Epic plant name", "Flower.name"})
    public void ValidatingPlantName_ValidField_NoErrors(String plantName) {
        ObjectError objectError = PlantValidator.validatePlantName(plantName);
        assertNull(objectError);
    }





}
