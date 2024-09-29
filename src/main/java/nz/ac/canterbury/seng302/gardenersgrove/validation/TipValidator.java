package nz.ac.canterbury.seng302.gardenersgrove.validation;

import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;


public class TipValidator {

    private static final String TIP_AMOUNT_ERROR = "tipAmountError";

    private TipValidator() {
        // Private constructor to prevent instantiation
    }

    public static boolean isPositiveTip(Integer tipAmount) {
        return tipAmount > 0;
    }

    public static boolean isValidTip(Integer tipAmount, User currentUser) {
        return tipAmount <= currentUser.getBloomBalance();
    }

    public static void doTipValidations(HttpSession session, Integer tipAmount, User currentUser) {
        if (!isPositiveTip(tipAmount)) {
            session.setAttribute(TIP_AMOUNT_ERROR, "Tip amount must be a positive integer");
        } else if (!isValidTip(tipAmount, currentUser)) {
            session.setAttribute(TIP_AMOUNT_ERROR, "Insufficient Bloom balance");
        }
        // If there is an error, add a flag to keep the modal open
        if (session.getAttribute(TIP_AMOUNT_ERROR) != null) {

            //Add tip amount to the session so we can add it to the model later in the controller
            session.setAttribute("tipInput", tipAmount);
        }
    }
}
