package nz.ac.canterbury.seng302.gardenersgrove.validation;

import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import org.springframework.ui.Model;


public class TipValidator {

//    private final static int MAX_TIP_AMOUNT;
//    TODO another time: calculate max tip amount based on case where the tipper and tippee
//     both have nearly the max amount of Blooms possible in database

    public static boolean isPositiveTip(Integer tipAmount) {
        if (tipAmount <= 0){
            return false;
        }
        return true;
    }

    public static boolean isValidTip(Integer tipAmount, User currentUser) {
        if (tipAmount > currentUser.getBloomBalance()){
            return false;
        }
        return true;
    }

    public static void doTipValidations(HttpSession session, Integer tipAmount, User currentUser) {
        if (!isPositiveTip(tipAmount)) {
            session.setAttribute("tipAmountError", "Tip amount must be a positive number");
        } else if (!isValidTip(tipAmount, currentUser)) {
            session.setAttribute("tipAmountError", "Insufficient Bloom balance");
        }
        // If there is an error, add a flag to keep the modal open
        if (session.getAttribute("tipAmountError") != null) {

            //Add tip amount to the session so we can add it to the model later in the controller
            session.setAttribute("tipInput", tipAmount);
        }
    }
}
