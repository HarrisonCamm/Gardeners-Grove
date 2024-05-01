package nz.ac.canterbury.seng302.gardenersgrove.unit.validation;

import nz.ac.canterbury.seng302.gardenersgrove.validation.PlantValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.validation.ObjectError;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PlantValidatorTests {

    @ParameterizedTest
    @CsvSource({"''", "XXX_PlantName_XXX", "$$ROSE$$$"})
    public void ValidatingPlantName_InvalidField_HasErrors(String plantName) {
        ObjectError objectError = PlantValidator.validatePlantName(plantName);
        assertNotNull(objectError);
    }

    @ParameterizedTest
    @CsvSource({"Rose", "Epic plant name", "Flower.name"})
    public void ValidatingPlantName_ValidField_NoErrors(String plantName) {
        ObjectError objectError = PlantValidator.validatePlantName(plantName);
        assertNull(objectError);
    }

    @ParameterizedTest
    @CsvSource({
            "-1",
            "" + Long.MIN_VALUE,
            "'1.1.'",
            "'1..1'",
            "'1,,1'",
            "'1,1,'",
            "'-1.1.'",
            "'-1..1'",
            "'-1,,1'",
            "'-1,1,'"})
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

    @Test
    public void ValidatingPlantDescription_InvalidField_HasErrors() {
        String string513 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis ultricies nisl non nulla tincidunt, non sagittis justo auctor. Curabitur vehicula euismod arcu sit amet tincidunt. Sed sit amet sem ultricies, vestibulum magna ac, fermentum metus. Phasellus in nulla nec libero congue volutpat. Suspendisse potenti. Sed interdum magna non magna fermentum, id ullamcorper nibh feugiat. Morbi elementum bibendum turpis, et lacinia elit cursus non. Maecenas vitae arcu augue. Fusce pharetra ex a rhoncus auctor. Integer suscipit felis id ex lobortis, nec varius mi tempus. Donec placerat magna euismod purus sodales posuere. Proin nec nunc nunc. Nulla auctor, libero quis tincidunt scelerisque, libero purus malesuada velit, vel hendrerit leo felis sit amet nisi. Donec vel congue quam. Suspendisse potenti. Fusce tristique viverra felis, sed ultricies mauris scelerisque eget. Aenean ac sem turpis.a";
        ObjectError objectError = PlantValidator.validatePlantDescription(string513);
        assertNotNull(objectError);
    }

    @ParameterizedTest
    @CsvSource({"A cool plant", "''"})
    public void ValidatingPlantDescription_ValidField_NoErrors(String plantDescription) {
        ObjectError objectError = PlantValidator.validatePlantDescription(plantDescription);
        assertNull(objectError);
    }





}
