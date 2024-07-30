package nz.ac.canterbury.seng302.gardenersgrove.validation;

import org.springframework.validation.FieldError;

public class FieldErrorFactory {
    public FieldError createFieldError(String objectName, String field, String message) {
        return new FieldError(objectName, field, message);
    }
}
