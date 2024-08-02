package nz.ac.canterbury.seng302.gardenersgrove.validation;
import org.springframework.ui.Model;

import java.util.regex.Pattern;

public class TagValidator {

    public static boolean isValidTag(String tag) {
        // Regular expression to match alphanumeric characters, spaces, dots, commas, hyphens, and apostrophes
        String regex = "^[\\p{L}0-9\\s_'-]+$";
        return Pattern.matches(regex, tag);
    }

    public static boolean isValidLength(String tag) {
        return tag.length()<=25;
    }

    private static boolean isAppropriateName(String possibleTerms) {
        return possibleTerms.equals("null");

    }


    public static void doTagValidations(Model model, String tag, String possibleTerms) {
        if (!isValidTag(tag)) {
            model.addAttribute("tagTextError", "The tag name must only contain alphanumeric characters, spaces, -, _, or '");
        }
        if (!isValidLength(tag)) {
            model.addAttribute("tagLengthError", "A tag cannot exceed 25 characters");
        }
        if (!isAppropriateName(possibleTerms)) {
            model.addAttribute("profanityTagError", "Profanity or inappropriate language detected");
        }

    }
}