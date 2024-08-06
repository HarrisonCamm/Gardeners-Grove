package nz.ac.canterbury.seng302.gardenersgrove.validation;
import org.springframework.ui.Model;

import java.util.regex.Pattern;

public class TagValidator {
    private final static int MAX_TAG_LENGTH = 25;

    public static boolean isValidTag(String tag) {
        if (tag.trim().isEmpty()){
            return false;
        }
        // Regular expression to match alphanumeric characters, spaces, dashes, underscores, and apostrophes
        String regex = "^[\\p{L}0-9\\s_'-]+$";
        return Pattern.matches(regex, tag);
    }

    public static boolean isValidLength(String tag) {
        return tag.length()<=MAX_TAG_LENGTH;
    }

    //Used for tag profanity moderation
    public static boolean isAppropriateName(String possibleTerms) {
        return possibleTerms.equals("null");

    }


    public static void doTagValidations(Model model, String tag, String possibleTerms) {
        if (!isValidTag(tag)) {
            model.addAttribute("tagTextError", "The tag name must only contain alphanumeric characters, spaces, -, _, or '");
        }
        if (!isValidLength(tag)) {
            model.addAttribute("tagLengthError", "A tag cannot exceed " + MAX_TAG_LENGTH + " characters");
        }
        if (!isAppropriateName(possibleTerms)) {
            model.addAttribute("profanityTagError", "Profanity or inappropriate language detected");
        }

    }
}