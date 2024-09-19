package nz.ac.canterbury.seng302.gardenersgrove.validation;

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

    public static void doTipValidations(Model model, Integer tipAmount, User currentUser) {
        if (!isPositiveTip(tipAmount)) {
            model.addAttribute("tipAmountError", "Tip amount must be a positive number");
        } else if (!isValidTip(tipAmount, currentUser)) {
            model.addAttribute("tipAmountError", "Insufficient Bloom balance");
        }
        // If there is an error, add a flag to keep the modal open
        if (model.containsAttribute("tipAmountError")) {
            model.addAttribute("showTipModal", true);
            model.addAttribute("tipInput", tipAmount);
        }
    }
}
