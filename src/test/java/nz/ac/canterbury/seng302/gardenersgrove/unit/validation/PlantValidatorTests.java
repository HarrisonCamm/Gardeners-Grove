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
    @CsvSource(value = {"sfcrtrbunrjqdpqsqdcfnunpzmatzvqwlhmouukyjzymuydxkmqzflupmqtypiupgupetidvkicweljpzlkufqgerlapwkuhl" +
            "uvsmbptsdyvdgpoxvkrbaaelpfnmdgtlwkyuigtqnciuzzviobgeisyeqgtdiumxwumgtuhwlnkdtgpfvbpzugncscningysdlauvf" +
            "vdrbhgwwhbstpabjddabjibvsrjkrgbjeyqvzlrzyxvcinjyglesyucftsfcrtrbunrjqdpqsqdcfnunpzmatzvqwlhmouukyjzymuydxkmqzflupmqtypiupgupetidvkicweljpzlkufqgerlapwkuhl\" +\n" +
            "            \"uvsmbptsdyvdgpoxvkrbaaelpfnmdgtlwkyuigtqnciuzzviobgeisyeqgtdiumxwumgtuhwlnkdtgpfvbpzugncscningysdlauvf\" +\n" +
            "            \"vdrbhgwwhbstpabjddabjibvsrjkrgbjeyqvzlrzyxvcinjyglesyucft"})
    public void ValidatingPlant_PlantDescription_Invalid(String name) {
        ObjectError objectError = PlantValidator.validatePlantDescription(name);
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
