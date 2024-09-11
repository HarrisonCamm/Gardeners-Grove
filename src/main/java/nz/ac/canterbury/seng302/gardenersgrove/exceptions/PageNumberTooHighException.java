package nz.ac.canterbury.seng302.gardenersgrove.exceptions;

public class PageNumberTooHighException extends RuntimeException {
    public PageNumberTooHighException(String message) {
        super(message);
    }
}
